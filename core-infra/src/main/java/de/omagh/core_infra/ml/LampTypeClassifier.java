package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

/**
 * Interface for grow light type classifiers.
 */
public interface LampTypeClassifier {
    /**
     * Analyze a photo of a lamp and update the classification result.
     */
    void classify(Bitmap bitmap);

    /**
     * Returns the last detected lamp type.
     */
    String getLastResult();
}