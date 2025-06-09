package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.view.PreviewView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.feature_measurement.CameraLightMeterX;
import de.omagh.lumibuddy.util.OnSwipeTouchListener;

public class MeasureFragment extends Fragment {
    private MeasureViewModel mViewModel;

    // Lamp type selection
    private Spinner lampTypeSpinner;
    private ViewFlipper measureFlipper;
    private View dliWidget;

    // DLI widget controls
    private Button preset12h, preset18h, preset24h, plusHour, minusHour;
    private TextView hourValue, dliWidgetValue;

    // CameraX measurement
    private PreviewView cameraPreview;
    private Button cameraMeasureButton;
    private CameraLightMeterX cameraLightMeterX;

    // Modern card views
    private View luxCard, ppfdCard, dliCard;
    private TextView luxValue, ppfdValue, dliValue;

    public static MeasureFragment newInstance() {
        return new MeasureFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measure, container, false);

        lampTypeSpinner = view.findViewById(R.id.lampTypeSpinner);
        measureFlipper = view.findViewById(R.id.measureFlipper);
        dliWidget = view.findViewById(R.id.dliWidget);

        // Bind cards for modern UI
        luxCard = view.findViewById(R.id.luxCard);
        ppfdCard = view.findViewById(R.id.ppfdCard);
        dliCard = view.findViewById(R.id.dliCard);

        // Bind value TextViews in each card
        luxValue = luxCard.findViewById(R.id.metricValue);
        ppfdValue = ppfdCard.findViewById(R.id.metricValue);
        dliValue = dliCard.findViewById(R.id.metricValue);

        // Set up icons, units, and colors for each card (do this once)
        setupCard(luxCard, R.drawable.ic_lux, "Lux", "lx", R.color.luxColor);
        setupCard(ppfdCard, R.drawable.ic_ppfd, "PPFD", "μmol/m²/s", R.color.ppfdColor);
        setupCard(dliCard, R.drawable.ic_dli, "DLI", "mol/m²/d", R.color.dliColor);

        // DLI widget controls
        preset12h = dliWidget.findViewById(R.id.preset12h);
        preset18h = dliWidget.findViewById(R.id.preset18h);
        preset24h = dliWidget.findViewById(R.id.preset24h);
        plusHour = dliWidget.findViewById(R.id.plusHour);
        minusHour = dliWidget.findViewById(R.id.minusHour);
        hourValue = dliWidget.findViewById(R.id.hourValue);
        dliWidgetValue = dliWidget.findViewById(R.id.dliValue);

        cameraMeasureButton = view.findViewById(R.id.cameraMeasureButton);
        cameraPreview = view.findViewById(R.id.cameraPreview);

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
        mViewModel = new ViewModelProvider(this).get(MeasureViewModel.class);

        // LiveData observers update card values
        mViewModel.getLux().observe(getViewLifecycleOwner(), lux ->
                luxValue.setText(String.format("%.1f", lux)));

        mViewModel.getPPFD().observe(getViewLifecycleOwner(), ppfd ->
                ppfdValue.setText(String.format("%.1f", ppfd)));

        mViewModel.getDLI().observe(getViewLifecycleOwner(), dli ->
                dliValue.setText(String.format("%.2f", dli)));

        // DLI hour observer
        mViewModel.getHours().observe(getViewLifecycleOwner(), hours -> {
            hourValue.setText(hours + " h");
            preset12h.setBackgroundResource(hours == 12 ? R.drawable.chip_selected : R.drawable.chip_unselected);
            preset18h.setBackgroundResource(hours == 18 ? R.drawable.chip_selected : R.drawable.chip_unselected);
            preset24h.setBackgroundResource(hours == 24 ? R.drawable.chip_selected : R.drawable.chip_unselected);
        });

        // DLI observer (widget)
        mViewModel.getDLI().observe(getViewLifecycleOwner(), dli -> {
            dliWidgetValue.setText(String.format("%.2f", dli));
        });

        // Lamp type spinner: update ViewModel when user selects an item
        lampTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                MeasureViewModel.LampType type = MeasureViewModel.LampType.values()[position];
                mViewModel.setLampType(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Ensure spinner stays in sync with ViewModel state
        mViewModel.getLampType().observe(getViewLifecycleOwner(), lampType -> {
            if (lampType != null && lampTypeSpinner.getSelectedItemPosition() != lampType.ordinal()) {
                lampTypeSpinner.setSelection(lampType.ordinal());
            }
        });

        // --- CameraX Measurement Integration ---
        cameraLightMeterX = new CameraLightMeterX(requireActivity(), cameraPreview);

        cameraMeasureButton.setOnClickListener(v -> {
            cameraPreview.setVisibility(View.VISIBLE);
            cameraLightMeterX.startCamera();

            // Show tip dialog for user (diffuser advice)
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Tip")
                    .setMessage("Place a single white sheet of paper over the camera for best accuracy, then tap OK.")
                    .setPositiveButton("OK", (d, w) -> {
                        cameraLightMeterX.analyzeFrame(new CameraLightMeterX.ResultCallback() {
                            @Override
                            public void onResult(float meanR, float meanG, float meanB) {
                                float pseudoLux = meanR + meanG + meanB;
                                requireActivity().runOnUiThread(() -> {
                                    mViewModel.setLux(pseudoLux);
                                    cameraPreview.setVisibility(View.GONE);

                                    // Auto-detect lamp type and show warning if needed
                                    String[] analysis = autoDetectLampType(meanR, meanG, meanB);
                                    String lampSuggestion = analysis[0];
                                    String warning = analysis[1];

                                    int index = lampTypeStringToIndex(lampSuggestion);
                                    if (index >= 0) lampTypeSpinner.setSelection(index);

                                    if (warning != null) {
                                        new androidx.appcompat.app.AlertDialog.Builder(getContext())
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
                        });
                    })
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

    private int lampTypeStringToIndex(String suggestion) {
        switch (suggestion) {
            case "Sunlight":
            case "White/Sunlight":
                return 0;
            case "White LED (neutral)":
                return 1;
            case "Warm LED":
                return 2;
            case "Blurple LED":
                return 3;
            case "HPS":
                return 4;
            default:
                return -1;
        }
    }

    // Helper to DRY card setup
    private void setupCard(View card, int iconRes, String label, String unit, int valueColorRes) {
        ((TextView) card.findViewById(R.id.metricLabel)).setText(label);
        ((TextView) card.findViewById(R.id.metricUnit)).setText(unit);
        ((ImageView) card.findViewById(R.id.metricIcon)).setImageResource(iconRes);
        ((TextView) card.findViewById(R.id.metricValue)).setTextColor(getResources().getColor(valueColorRes));
    }
}
