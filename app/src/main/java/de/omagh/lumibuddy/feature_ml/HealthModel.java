package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Interface for plant health status models.
 */
public interface HealthModel {
    void loadModel();

    void analyzeImage(Bitmap input);

    String getResult();
}