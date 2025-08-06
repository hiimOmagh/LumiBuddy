package de.omagh.feature_measurement.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_measurement.di.DaggerMeasurementComponent;
import de.omagh.feature_measurement.di.MeasurementComponent;

import de.omagh.core_domain.model.Measurement;
import de.omagh.core_infra.measurement.CameraLightMeterX;
import de.omagh.core_infra.ar.ARMeasureOverlay;
import de.omagh.core_infra.ar.HeatmapOverlayView;
import de.omagh.feature_measurement.R;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.shared_ml.LampIdentifier;
import de.omagh.core_infra.util.OnSwipeTouchListener;
import de.omagh.core_infra.util.PermissionUtils;
import timber.log.Timber;

import com.google.ar.core.ArCoreApk;

public class MeasureFragment extends Fragment {
    @Inject
    MeasureViewModelFactory viewModelFactory;
    private MeasureViewModel mViewModel;
    // Lamp type selection
    private Spinner lampTypeSpinner;
    private TextView calibrationFactorText;
    private ViewFlipper measureFlipper;
    private View dliWidget;
    private java.util.List<LampProduct> lampList;
    // DLI widget controls
    private Button preset12h;
    private Button preset18h;
    private Button preset24h;
    private TextView hourValue, dliWidgetValue;
    // CameraX measurement
    private PreviewView cameraPreview;
    private de.omagh.core_infra.ar.HeatmapOverlayView heatmapOverlay;
    private Button cameraMeasureButton;
    private CameraLightMeterX cameraLightMeterX;
    private androidx.activity.result.ActivityResultLauncher<String> cameraPermissionLauncher;
    private androidx.activity.result.ActivityResultLauncher<String> locationPermissionLauncher;
    // AR overlay integration
    private boolean enableAROverlay = false;
    private de.omagh.core_infra.ar.AROverlayRenderer arOverlayRenderer;
    private TextView luxValue, ppfdValue, dliValue;

    public static MeasureFragment newInstance() {
        return new MeasureFragment();
    }

    private boolean isArSupported() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(requireContext());
        return availability.isSupported();
    }

    @Override
    public void onAttach(@NonNull android.content.Context context) {
        super.onAttach(context);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        MeasurementComponent component = DaggerMeasurementComponent.factory().create(core);
        component.inject(this);
        viewModelFactory = component.viewModelFactory();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measure, container, false);
        mViewModel = new ViewModelProvider(this, viewModelFactory).get(MeasureViewModel.class);
        enableAROverlay = mViewModel.isArOverlayEnabled();

        // Lamp selection spinner
        lampTypeSpinner = view.findViewById(R.id.lampTypeSpinner);
        calibrationFactorText = view.findViewById(R.id.calibrationFactorText);
        lampList = mViewModel.getLampProfiles();

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (LampProduct p : lampList) {
            adapter.add(p.name);
        }
        lampTypeSpinner.setAdapter(adapter);
        String activeId = mViewModel.getLampProfileId().getValue();
        for (int i = 0; i < lampList.size(); i++) {
            if (lampList.get(i).id.equalsIgnoreCase(activeId)) {
                lampTypeSpinner.setSelection(i);
                break;
            }
        }
        measureFlipper = view.findViewById(R.id.measureFlipper);
        dliWidget = view.findViewById(R.id.dliWidget);

        // Default displayed metric based on user preference
        String prefUnit = mViewModel.getPreferredUnit();
        if ("PPFD".equalsIgnoreCase(prefUnit)) {
            measureFlipper.setDisplayedChild(1);
        } else if ("DLI".equalsIgnoreCase(prefUnit)) {
            measureFlipper.setDisplayedChild(2);
        } else {
            measureFlipper.setDisplayedChild(0);
        }
        updateDLIWidgetVisibility(measureFlipper.getDisplayedChild());

        // Bind cards for modern UI
        // Modern card views
        View luxCard = view.findViewById(R.id.luxCard);
        View ppfdCard = view.findViewById(R.id.ppfdCard);
        View dliCard = view.findViewById(R.id.dliCard);

        // Bind value TextViews in each card
        luxValue = luxCard.findViewById(R.id.metricValue);
        ppfdValue = ppfdCard.findViewById(R.id.metricValue);
        dliValue = dliCard.findViewById(R.id.metricValue);

        // Set up icons, units, and colors for each card
        setupCard(luxCard, R.drawable.ic_lux, "Lux", "lx", R.color.luxColor);
        setupCard(ppfdCard, R.drawable.ic_ppfd, "PPFD", "μmol/m²/s", R.color.ppfdColor);
        setupCard(dliCard, R.drawable.ic_dli, "DLI", "mol/m²/d", R.color.dliColor);

        // DLI widget controls
        preset12h = dliWidget.findViewById(R.id.preset12h);
        preset18h = dliWidget.findViewById(R.id.preset18h);
        preset24h = dliWidget.findViewById(R.id.preset24h);
        Button plusHour = dliWidget.findViewById(R.id.plusHour);
        Button minusHour = dliWidget.findViewById(R.id.minusHour);
        hourValue = dliWidget.findViewById(R.id.hourValue);
        dliWidgetValue = dliWidget.findViewById(R.id.dliValue);

        cameraMeasureButton = view.findViewById(R.id.cameraMeasureButton);
        cameraPreview = view.findViewById(R.id.cameraPreview);
        heatmapOverlay = view.findViewById(R.id.heatmapOverlay);
        SwitchCompat arToggle = view.findViewById(R.id.arToggle);
        arToggle.setChecked(enableAROverlay);
        arToggle.setOnCheckedChangeListener((btn, checked) -> {
            if (checked && !isArSupported()) {
                android.widget.Toast.makeText(getContext(), R.string.device_not_supported, android.widget.Toast.LENGTH_LONG).show();
                btn.setChecked(false);
                return;
            }
            enableAROverlay = checked;
            mViewModel.setArOverlayEnabled(checked);
            if (checked) {
                if (arOverlayRenderer == null) {
                    arOverlayRenderer = new ARMeasureOverlay(heatmapOverlay);
                    arOverlayRenderer.init();
                }
            } else {
                if (arOverlayRenderer != null) {
                    arOverlayRenderer.cleanup();
                    arOverlayRenderer = null;
                }
            }
        });

        // Swiping for measurement selection
        view.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeTop() {
                showNextValue();
            }

            @Override
            public void onSwipeBottom() {
                showPreviousValue();
            }
        });

        // DLI widget: hour presets and +/- controls
        preset12h.setOnClickListener(v -> mViewModel.setHours(12));
        preset18h.setOnClickListener(v -> mViewModel.setHours(18));
        preset24h.setOnClickListener(v -> mViewModel.setHours(24));
        plusHour.setOnClickListener(v -> {
            Integer cur = mViewModel.getHours().getValue();
            if (cur != null && cur < 24) mViewModel.setHours(cur + 1);
        });
        minusHour.setOnClickListener(v -> {
            Integer cur = mViewModel.getHours().getValue();
            if (cur != null && cur > 1) mViewModel.setHours(cur - 1);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        locationPermissionLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        mViewModel.refreshSunlightEstimate();
                    } else {
                        PermissionUtils.showPermissionDenied(this,
                                getString(R.string.location_permission_required));
                    }
                });

        if (mViewModel.isAutoSunlightEstimationEnabled()) {
            boolean hasFine = PermissionUtils.hasPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            boolean hasCoarse = PermissionUtils.hasPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            if (!hasFine && !hasCoarse) {
                PermissionUtils.requestPermissionWithRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        getString(R.string.location_permission_rationale),
                        locationPermissionLauncher);
            } else {
                mViewModel.refreshSunlightEstimate();
            }
        }

        if (enableAROverlay && isArSupported()) {
            arOverlayRenderer = new ARMeasureOverlay(heatmapOverlay);
            arOverlayRenderer.init();
        } else if (enableAROverlay) {
            android.widget.Toast.makeText(getContext(), R.string.device_not_supported, android.widget.Toast.LENGTH_LONG).show();
            enableAROverlay = false;
        }

        // LiveData observers update card values
        mViewModel.getLux().observe(getViewLifecycleOwner(), lux ->
                luxValue.setText(String.format("%.1f", lux)));

        mViewModel.getPPFD().observe(getViewLifecycleOwner(), ppfd ->
                ppfdValue.setText(String.format("%.1f", ppfd)));

        mViewModel.getDLI().observe(getViewLifecycleOwner(), dli ->
                dliValue.setText(String.format("%.2f", dli)));

        // DLI hour observer
        mViewModel.getHours().observe(getViewLifecycleOwner(), this::onChanged);

        // DLI observer (widget)
        mViewModel.getDLI().observe(getViewLifecycleOwner(), dli ->
                dliWidgetValue.setText(String.format("%.2f", dli)));

        // Show calibration factor used for PPFD conversion
        mViewModel.getCalibrationFactor().observe(getViewLifecycleOwner(), factor ->
                calibrationFactorText.setText(getString(R.string.calibration_factor, factor)));

        // Lamp type spinner: update ViewModel when user selects an item
        lampTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                if (position >= 0 && position < lampList.size()) {
                    mViewModel.setLampProfileId(lampList.get(position).id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no-op
            }
        });

        // Ensure spinner stays in sync with ViewModel state
        mViewModel.getLampProfileId().observe(getViewLifecycleOwner(), id -> {
            if (id == null) return;
            for (int i = 0; i < lampList.size(); i++) {
                if (id.equalsIgnoreCase(lampList.get(i).id)) {
                    if (lampTypeSpinner.getSelectedItemPosition() != i) {
                        lampTypeSpinner.setSelection(i);
                    }
                    break;
                }
            }
        }); // <-- All brackets closed correctly here!

        // --- CameraX Measurement Integration ---
        cameraLightMeterX = new CameraLightMeterX(requireActivity(), cameraPreview);
        cameraPermissionLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        cameraMeasureButton.performClick();
                    } else {
                        PermissionUtils.showPermissionDenied(this,
                                getString(R.string.camera_permission_required));
                    }
                });

        cameraMeasureButton.setOnClickListener(v -> {
            if (!PermissionUtils.hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                PermissionUtils.requestPermissionWithRationale(
                        this,
                        Manifest.permission.CAMERA,
                        getString(R.string.camera_permission_rationale),
                        cameraPermissionLauncher);
                return;
            }
            cameraPreview.setVisibility(View.VISIBLE);
            cameraLightMeterX.startCamera();

            // Show tip dialog for user (diffuser advice)
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Tip")
                    .setMessage("Place a single white sheet of paper over the camera for best accuracy, then tap OK.")
                    .setPositiveButton("OK", (d, w) ->
                            cameraLightMeterX.analyzeFrameWithGrid(10, 10, new CameraLightMeterX.GridResultCallback() {
                                @Override
                                public void onResult(float meanR, float meanG, float meanB, float[][] intensity) {
                                    float pseudoLux = meanR + meanG + meanB;
                                    requireActivity().runOnUiThread(() -> {
                                        mViewModel.setLux(pseudoLux, "Camera");
                                        cameraPreview.setVisibility(View.GONE);

                                        if (enableAROverlay && arOverlayRenderer != null) {
                                            Measurement m = new Measurement();
                                            m.lux = pseudoLux;
                                            heatmapOverlay.setVisibility(View.VISIBLE);
                                            arOverlayRenderer.renderOverlay(new android.graphics.Canvas(), m, intensity);
                                        }

                                        // Auto-detect lamp type and show warning if needed
                                        String[] analysis = autoDetectLampType(meanR, meanG, meanB);
                                        String lampSuggestion = analysis[0];
                                        String warning = analysis[1];

                                        if (mViewModel.isMlFeaturesEnabled()) {
                                            Bitmap sample = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                                            sample.eraseColor(Color.rgb((int) Math.min(255, meanR), (int) Math.min(255, meanG), (int) Math.min(255, meanB)));
                                            Toast.makeText(getContext(), R.string.calibrating, Toast.LENGTH_SHORT).show();
                                            mViewModel.identifyLamp(sample)
                                                    .observe(getViewLifecycleOwner(), preds -> {
                                                        if (preds == null) {
                                                            Toast.makeText(getContext(), R.string.ml_inference_error, Toast.LENGTH_LONG).show();
                                                            return;
                                                        }
                                                        LampIdentifier.Prediction top = preds.isEmpty() ? null : preds.get(0);
                                                        Timber.tag("MeasureFragment").d(
                                                                "Lamp identifier result=%s conf=%.2f",
                                                                top != null ? top.getLabel() : null,
                                                                top != null ? top.getConfidence() : 0f);
                                                        if (top == null || top.getLabel() == null) {
                                                            Toast.makeText(getContext(), R.string.ml_low_confidence, Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }

                                        int index = lampTypeStringToIndex(lampSuggestion);
                                        if (index >= 0) lampTypeSpinner.setSelection(index);

                                        if (warning != null) {
                                            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                                    .setTitle("Spectrum Warning")
                                                    .setMessage(warning)
                                                    .setPositiveButton("OK", null)
                                                    .show();
                                        }
                                        android.widget.Toast.makeText(getContext(), "Measurement complete!", android.widget.Toast.LENGTH_SHORT).show();
                                    });
                                }

                                @Override
                                public void onError(String message) {
                                    requireActivity().runOnUiThread(() -> {
                                        android.widget.Toast.makeText(getContext(), "Camera error: " + message, android.widget.Toast.LENGTH_LONG).show();
                                        cameraPreview.setVisibility(View.GONE);
                                    });
                                }
                            }))
                    .show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewModel.startMeasuring();
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.stopMeasuring();
        if (cameraLightMeterX != null) cameraLightMeterX.stopCamera();
        if (arOverlayRenderer != null) arOverlayRenderer.cleanup();
    }

    // Show next/prev measurement screen and handle DLI widget visibility
    private void showNextValue() {
        int current = measureFlipper.getDisplayedChild();
        int total = measureFlipper.getChildCount();
        if (current < total - 1) {
            measureFlipper.showNext();
            updateDLIWidgetVisibility(measureFlipper.getDisplayedChild());
        }
    }

    private void showPreviousValue() {
        int current = measureFlipper.getDisplayedChild();
        if (current > 0) {
            measureFlipper.showPrevious();
            updateDLIWidgetVisibility(measureFlipper.getDisplayedChild());
        }
    }

    private void updateDLIWidgetVisibility(int displayedChild) {
        dliWidget.setVisibility(displayedChild == 2 ? View.VISIBLE : View.GONE);
    }

    // --- Utility methods ---

    /**
     * Attempts to auto-detect the lamp spectrum type based on mean RGB values.
     * Returns: [lampTypeString, warningStringOrNull]
     */
    private String[] autoDetectLampType(float meanR, float meanG, float meanB) {
        float sum = meanR + meanG + meanB;
        if (sum == 0) return new String[]{"Unknown", "Sensor reading error."};
        float rRatio = meanR / sum;
        float gRatio = meanG / sum;
        float bRatio = meanB / sum;
        String type, warning = null;
        if (Math.abs(rRatio - gRatio) < 0.15f && Math.abs(gRatio - bRatio) < 0.15f) {
            type = "White/Sunlight";
        } else if (rRatio > 0.4f && bRatio > 0.4f && gRatio < 0.2f) {
            type = "Blurple LED";
            warning = "Warning: Unusual spectrum (purple/pink light).";
        } else if (gRatio > 0.5f && rRatio > 0.3f && bRatio < 0.1f) {
            type = "HPS";
            warning = "Warning: Unusual spectrum (yellow/orange).";
        } else if (bRatio > 0.6f && rRatio < 0.2f) {
            type = "Blue-dominant";
            warning = "Warning: High blue light.";
        } else if (rRatio > 0.6f && bRatio < 0.2f) {
            type = "Red-dominant";
            warning = "Warning: High red light.";
        } else {
            type = "Unknown/Other";
            warning = "Warning: Unusual or unknown spectrum.";
        }
        return new String[]{type, warning};
    }

    /**
     * Tries to match the suggested lamp type string to a lampList index. Returns -1 if not found.
     */
    private int lampTypeStringToIndex(String suggestion) {
        if (suggestion == null) return -1;
        String lower = suggestion.toLowerCase();
        for (int i = 0; i < lampList.size(); i++) {
            if (lampList.get(i).name.toLowerCase().contains(lower)) {
                return i;
            }
        }
        return -1;
    }

    // Helper to DRY card setup
    private void setupCard(View card, int iconRes, String label, String unit, int valueColorRes) {
        ((TextView) card.findViewById(R.id.metricLabel)).setText(label);
        ((TextView) card.findViewById(R.id.metricUnit)).setText(unit);
        ((ImageView) card.findViewById(R.id.metricIcon)).setImageResource(iconRes);
        ((TextView) card.findViewById(R.id.metricValue)).setTextColor(
                ContextCompat.getColor(requireContext(), valueColorRes));
    }

    private void onChanged(Integer hours) {
        hourValue.setText(hours + " h");
        preset12h.setBackgroundResource(hours == 12 ? R.drawable.chip_selected : R.drawable.chip_unselected);
        preset18h.setBackgroundResource(hours == 18 ? R.drawable.chip_selected : R.drawable.chip_unselected);
        preset24h.setBackgroundResource(hours == 24 ? R.drawable.chip_selected : R.drawable.chip_unselected);
    }
}
