package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Stub implementation of {@link HealthStatusClassifier}.
 * <p>
 * Always reports "Healthy" and logs invocations. Hook up a trained model
 * in the future to detect disease or stress symptoms.
 */
public class BasicHealthStatusClassifier implements HealthStatusClassifier {

    private static final String TAG = "BasicHealthStatusClassifier";
    private String lastResult = "Healthy";

    @Override
    public void classify(Bitmap bitmap) {
        Timber.tag(TAG).d("classify() called with bitmap=" + bitmap);
        lastResult = "Healthy";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}