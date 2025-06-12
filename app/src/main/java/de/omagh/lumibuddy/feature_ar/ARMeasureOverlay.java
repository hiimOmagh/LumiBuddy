package de.omagh.lumibuddy.feature_ar;

import android.graphics.Canvas;

import de.omagh.lumibuddy.data.model.Measurement;

/**
 * Stub implementation of {@link AROverlayRenderer}.
 * Currently draws nothing but provides a hook for future AR overlays.
 */
public class ARMeasureOverlay implements AROverlayRenderer {

    @Override
    public void init() {
        // No-op for now
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement) {
        // Placeholder - actual drawing will be implemented later
    }

    @Override
    public void cleanup() {
        // No-op for now
    }
}