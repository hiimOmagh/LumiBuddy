package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Stub implementation of {@link LampTypeClassifier}.
 * Always returns "Unknown Lamp" and logs calls.
 * Provided only as a demo; replace with a real implementation.
 */
public class BasicLampTypeClassifier implements LampTypeClassifier {

    private static final String TAG = "BasicLampTypeClassifier";
    private String lastResult;

    @Override
    public void classify(Bitmap bitmap) {
        Timber.tag(TAG).d("classify() called with bitmap=%s", bitmap);
        lastResult = "Unknown Lamp";
    }

    @Override
    public String getLastResult() {
        return lastResult;
    }
}