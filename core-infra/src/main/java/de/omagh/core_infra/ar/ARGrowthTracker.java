package de.omagh.core_infra.ar;

import android.graphics.Bitmap;

/**
 * Interface for tracking plant growth using AR visualizations.
 */
public interface ARGrowthTracker {
    /**
     * Initialize resources needed for growth tracking.
     */
    void init();

    /**
     * Process a frame or image of the plant and update any AR overlays.
     */
    void trackGrowth(Bitmap currentView);

    /**
     * Release any allocated resources.
     */
    void cleanup();
}