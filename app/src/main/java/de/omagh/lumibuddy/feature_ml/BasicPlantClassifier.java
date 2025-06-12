package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Simple stub implementation of {@link PlantClassifier}.
 * <p>
 * Always returns "Unknown" and logs method invocations. Replace this
 * class with a real ML Kit or TensorFlow classifier in the future.
 */
public class BasicPlantClassifier implements PlantClassifier {

    private static final String TAG = "BasicPlantClassifier";
    private String lastResult;

    @Override
    public void classify(Bitmap bitmap) {
        Log.d(TAG, "classify() called with bitmap=" + bitmap);
        lastResult = "Unknown";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}