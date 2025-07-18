package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Simple stub implementation of {@link PlantClassifier}.
 * Always returns "Unknown" and logs method invocations.
 * This class exists only for demo/testing purposes and should be replaced
 * with a real ML-based classifier when available.
 */
public class BasicPlantClassifier implements PlantClassifier {

    private static final String TAG = "BasicPlantClassifier";
    private String lastResult;

    @Override
    public void classify(Bitmap bitmap) {
        Timber.tag(TAG).d("classify() called with bitmap=%s", bitmap);
        lastResult = "Unknown";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}