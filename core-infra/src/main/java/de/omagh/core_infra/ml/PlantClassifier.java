package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

/**
 * Interface for plant identification classifiers.
 */
public interface PlantClassifier {
    /**
     * Analyze the supplied image and update internal result state.
     */
    void classify(Bitmap bitmap);

    /**
     * Returns the last classification result, or {@code null} if no image has been processed.
     */
    String getLastResult();
}