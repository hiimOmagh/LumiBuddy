package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Interface for plant identification classifiers.
 * <p>
 * Implementations should analyze a bitmap and return a plant name or ID.
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