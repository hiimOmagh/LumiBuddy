package de.omagh.core_infra.ar;

import android.graphics.Canvas;
import android.view.View;

import de.omagh.core_infra.ar.HeatmapOverlayView;

import de.omagh.core_domain.model.Measurement;
import timber.log.Timber;

/**
 * Stub implementation of {@link AROverlayRenderer}.
 * This class does not perform any real AR rendering yet. It simply logs
 * the calls it receives and acts as an integration point for a future
 * ARCore based overlay renderer.
 */
public class ARMeasureOverlay implements AROverlayRenderer {

    private static final String TAG = "ARMeasureOverlay";
    private final HeatmapOverlayView overlayView;

    public ARMeasureOverlay(HeatmapOverlayView view) {
        this.overlayView = view;
    }

    @Override
    public void init() {
        Timber.tag(TAG).d("init() called");
        if (overlayView != null) {
            overlayView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement, float[][] intensityGrid) {
        Timber.tag(TAG).d("renderOverlay() called with measurement=%s", measurement);
        if (overlayView != null && intensityGrid != null) {
            overlayView.setIntensityGrid(intensityGrid);
        }
    }

    @Override
    public void cleanup() {
        Timber.tag(TAG).d("cleanup() called");
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
        }
    }
}