package de.omagh.lumibuddy.feature_ar;

import android.graphics.Canvas;

import de.omagh.core_domain.model.Measurement;

/**
 * Contract for components that render augmented reality overlays on top
 * of live measurement data.
 * <p>
 * Production implementations may use ARCore to draw guides or visual cues
 * that help users align sensors or visualize growth. The stub implementation
 * simply logs calls without performing any drawing.
 */
public interface AROverlayRenderer {
    /**
     * Initialize any resources required for rendering.
     */
    void init();

    /**
     * Render the overlay using the provided canvas and measurement.
     *
     * @param canvas      canvas to draw on
     * @param measurement most recent light/environment measurement
     */
    void renderOverlay(Canvas canvas, Measurement measurement);

    /**
     * Release allocated resources when no longer needed.
     */

    void cleanup();
}