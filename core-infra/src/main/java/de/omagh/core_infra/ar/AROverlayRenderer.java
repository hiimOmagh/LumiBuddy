package de.omagh.core_infra.ar;

import android.graphics.Canvas;

import de.omagh.core_domain.model.Measurement;

/**
 * Contract for components that render augmented reality overlays on top
 * of live measurement data.
 */
public interface AROverlayRenderer {
    /**
     * Initialize any resources required for rendering.
     */
    void init();

    /**
     * Render the overlay using the provided canvas and measurement.
     */
    void renderOverlay(Canvas canvas, Measurement measurement);

    /**
     * Release allocated resources when no longer needed.
     */
    void cleanup();
}