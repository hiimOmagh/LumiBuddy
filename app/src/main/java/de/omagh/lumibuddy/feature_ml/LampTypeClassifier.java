package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Dummy implementation of {@link LampClassifierModel}.
 */
public class LampTypeClassifier implements LampClassifierModel {
    private String result = "Unknown Lamp";

    @Override
    public void loadModel() {
        // No-op for now
    }

    @Override
    public void analyzeImage(Bitmap input) {
        // Placeholder classification
        result = "Generic Lamp";
    }

    @Override
    public String getResult() {
        return result;
    }
}