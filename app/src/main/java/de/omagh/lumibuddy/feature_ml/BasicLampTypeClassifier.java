package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Stub implementation of {@link LampTypeClassifier}.
 * <p>
 * Always returns "Unknown Lamp" and logs calls. Replace with a real
 * model to detect bulb or fixture types automatically.
 */
public class BasicLampTypeClassifier implements LampTypeClassifier {

    private static final String TAG = "BasicLampTypeClassifier";
    private String lastResult;

    @Override
    public void classify(Bitmap bitmap) {
        Timber.tag(TAG).d("classify() called with bitmap=" + bitmap);
        lastResult = "Unknown Lamp";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}