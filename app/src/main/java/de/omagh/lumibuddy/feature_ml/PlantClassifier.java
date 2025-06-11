package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Dummy implementation of {@linkPlantRecognitionModel}.
 */
public class PlantClassifier implements PlantRecognitionModel {
    private String result = "Unknown";

    @Override
    public void loadModel() {
        // No-op for now
    }

    @Override
    public void analyzeImage(Bitmap input) {
        // Placeholder classification logic
        result = "Generic Plant";
    }

    @Override
    public String getResult() {
        return result;
    }
}