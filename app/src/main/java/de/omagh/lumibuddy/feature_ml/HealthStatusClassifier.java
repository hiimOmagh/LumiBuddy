package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Dummy implementation of {@link HealthModel}.
 */
public class HealthStatusClassifier implements HealthModel {
    private String status = "Healthy";

    @Override
    public void loadModel() {
        // No-op for now
    }

    @Override
    public void analyzeImage(Bitmap input) {
        // Placeholder always returns healthy
        status = "Healthy";
    }

    @Override
    public String getResult() {
        return status;
    }
}