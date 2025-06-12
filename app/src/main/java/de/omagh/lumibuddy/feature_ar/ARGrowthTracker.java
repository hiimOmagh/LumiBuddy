package de.omagh.lumibuddy.feature_ar;

import android.graphics.Bitmap;

/**
 * Interface for tracking plant growth using AR visualizations.
 * <p>
 * Implementations may use ARCore or other frameworks to align
 * a plant's current camera view with prior images and render growth
 * indicators in real time.
 */
public interface ARGrowthTracker {
    /**
     * Initialize resources needed for growth tracking.
     */
    void init();

    /**
     * Process a frame or image of the plant and update any AR overlays.
     *
     * @param currentView latest camera frame or photo of the plant
     */
    void trackGrowth(Bitmap currentView);

    /**
     * Release any allocated resources.
     */
    void cleanup();
}