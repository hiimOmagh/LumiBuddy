package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

/**
 * Interface for plant health status classifiers.
 */
public interface HealthStatusClassifier {
    /**
     * Analyze a plant image to detect its health condition.
     */
    void classify(Bitmap bitmap);

    /**
     * Returns the last detected health status string.
     */
    String getLastResult();
}