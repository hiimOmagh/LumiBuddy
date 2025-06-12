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
        if (input == null) {
            result = "Unknown";
            return;
        }
        int w = input.getWidth();
        int h = input.getHeight();
        long r = 0, g = 0, b = 0;
        int[] pixels = new int[w * h];
        input.getPixels(pixels, 0, w, 0, 0, w, h);
        for (int px : pixels) {
            r += (px >> 16) & 0xff;
            g += (px >> 8) & 0xff;
            b += px & 0xff;
        }
        float avgR = r / (float) pixels.length;
        float avgG = g / (float) pixels.length;
        if (avgG > avgR + 20) {
            result = "Basil";
        } else if (avgR > avgG + 20) {
            result = "Aloe";
        } else {
            result = "Generic Plant";
        }
    }

    @Override
    public String getResult() {
        return result;
    }
}