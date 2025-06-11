package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Interface for plant recognition models.
 */
public interface PlantRecognitionModel {
    /**
     * Load the underlying ML model.
     */
    void loadModel();

    /**
     * Analyze the given image.
     */
    void analyzeImage(Bitmap input);

    /**
     * Return the recognition result or confidence value.
     */
    String getResult();
}