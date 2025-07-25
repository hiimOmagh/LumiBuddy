package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Stub implementation of {@link HealthStatusClassifier}.
 * Always reports "Healthy" and logs invocations.
 * Serves purely as a sample until a proper classifier is integrated.
 */
public class BasicHealthStatusClassifier implements HealthStatusClassifier {

    private static final String TAG = "BasicHealthStatusClassifier";
    private String lastResult = "Healthy";

    @Override
    public void classify(Bitmap bitmap) {
        Timber.tag(TAG).d("classify() called with bitmap=%s", bitmap);
        lastResult = "Healthy";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}