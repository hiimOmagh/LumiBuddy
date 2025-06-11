package de.omagh.lumibuddy.feature_ar;

import android.graphics.Canvas;

import de.omagh.lumibuddy.data.model.Measurement;

/**
 * Interface for rendering AR measurement overlays.
 */
public interface AROverlayRenderer {
    /**
     * Initialize any resources needed for rendering.
     */
    void init();

    /**
     * Render the overlay for the given measurement on the supplied canvas.
     */
    void renderOverlay(Canvas canvas, Measurement measurement);

    /**
     * Clean up any resources.
     */
    void cleanup();
}