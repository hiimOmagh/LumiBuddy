package de.omagh.feature_measurement.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

import androidx.camera.view.PreviewView;
import androidx.fragment.app.FragmentActivity;

import com.google.ar.core.ArCoreApk;

import java.util.ArrayList;
import java.util.List;

import de.omagh.core_domain.model.Measurement;
import de.omagh.core_infra.ar.ARMeasureOverlay;
import de.omagh.core_infra.ar.HeatmapOverlayView;
import de.omagh.core_infra.measurement.CameraLightMeterX;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.shared_ml.LampIdentifier;
import de.omagh.shared_ml.IdentifierResult;

/**
 * Handles camera measurement, AR overlay rendering and analysis logic.
 */
public class MeasurementController {

    public interface Callback {
        void onComplete(int lampIndex, String warning);
        void onError(String message);
    }

    private final FragmentActivity activity;
    private final PreviewView previewView;
    private final HeatmapOverlayView heatmapOverlayView;
    private final MeasureViewModel viewModel;
    private final List<LampProduct> lampList;
    private final CameraLightMeterX cameraLightMeterX;
    private boolean enableAROverlay;
    private de.omagh.core_infra.ar.AROverlayRenderer arOverlayRenderer;

    public MeasurementController(FragmentActivity activity,
                                 PreviewView previewView,
                                 HeatmapOverlayView heatmapOverlayView,
                                 boolean enableAROverlay,
                                 MeasureViewModel viewModel,
                                 List<LampProduct> lampList) {
        this.activity = activity;
        this.previewView = previewView;
        this.heatmapOverlayView = heatmapOverlayView;
        this.enableAROverlay = enableAROverlay;
        this.viewModel = viewModel;
        this.lampList = lampList;
        this.cameraLightMeterX = new CameraLightMeterX(activity, previewView);
        if (enableAROverlay && isArSupported()) {
            arOverlayRenderer = new ARMeasureOverlay(heatmapOverlayView);
            arOverlayRenderer.init();
        }
    }

    public boolean isArSupported() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(activity);
        return availability.isSupported();
    }

    public void setArOverlayEnabled(boolean enabled) {
        this.enableAROverlay = enabled;
        if (enabled) {
            if (arOverlayRenderer == null) {
                arOverlayRenderer = new ARMeasureOverlay(heatmapOverlayView);
                arOverlayRenderer.init();
            }
        } else {
            if (arOverlayRenderer != null) {
                arOverlayRenderer.cleanup();
                arOverlayRenderer = null;
            }
            heatmapOverlayView.setVisibility(View.GONE);
        }
    }

    public void startMeasurement(Callback callback) {
        previewView.setVisibility(View.VISIBLE);
        cameraLightMeterX.startCamera();
        cameraLightMeterX.analyzeFrameWithGrid(10, 10, new CameraLightMeterX.GridResultCallback() {
            @Override
            public void onResult(float meanR, float meanG, float meanB, float[][] intensity) {
                float pseudoLux = meanR + meanG + meanB;
                activity.runOnUiThread(() -> {
                    viewModel.setLux(pseudoLux, "Camera");
                    previewView.setVisibility(View.GONE);

                    if (enableAROverlay && arOverlayRenderer != null) {
                        Measurement m = new Measurement();
                        m.lux = pseudoLux;
                        heatmapOverlayView.setVisibility(View.VISIBLE);
                        arOverlayRenderer.renderOverlay(new Canvas(), m, intensity);
                    }

                    String[] analysis = MeasurementAnalyzer.autoDetectLampType(meanR, meanG, meanB);
                    int index = MeasurementAnalyzer.lampTypeStringToIndex(lampList, analysis[0]);

                    if (viewModel.isMlFeaturesEnabled()) {
                        Bitmap frame = previewView.getBitmap();
                        if (frame != null) {
                            int size = Math.min(frame.getWidth(), frame.getHeight());
                            int x = (frame.getWidth() - size) / 2;
                            int y = (frame.getHeight() - size) / 2;
                            Bitmap cropped = Bitmap.createBitmap(frame, x, y, size, size);
                            Bitmap resized = Bitmap.createScaledBitmap(cropped, 224, 224, true);
                            viewModel.identifyLamp(resized).observe(activity, res -> {
                                if (!(res instanceof IdentifierResult.Success)) return;
                                List<LampIdentifier.Prediction> preds =
                                        ((IdentifierResult.Success<List<LampIdentifier.Prediction>>) res).getValue();
                                float threshold = viewModel.getMlThreshold();
                                List<LampIdentifier.Prediction> filtered = new ArrayList<>();
                                for (LampIdentifier.Prediction p : preds) {
                                    if (p.getConfidence() >= threshold) {
                                        filtered.add(p);
                                    }
                                }
                                LampIdentifier.Prediction top = filtered.isEmpty() ? null : filtered.get(0);
                                if (top == null) {
                                    // nothing to do, UI can display message if needed
                                }
                            });
                        }
                    }

                    callback.onComplete(index, analysis[1]);
                });
            }

            @Override
            public void onError(String message) {
                activity.runOnUiThread(() -> {
                    previewView.setVisibility(View.GONE);
                    callback.onError(message);
                });
            }
        });
    }

    public void cleanup() {
        cameraLightMeterX.stopCamera();
        if (arOverlayRenderer != null) {
            arOverlayRenderer.cleanup();
        }
    }
}
