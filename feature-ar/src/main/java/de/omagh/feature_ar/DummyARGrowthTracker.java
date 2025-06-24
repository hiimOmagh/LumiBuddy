package de.omagh.feature_ar;

import android.graphics.Bitmap;

import timber.log.Timber;

/**
 * Stub implementation of {@link ARGrowthTracker}.
 * <p>
 * This class performs no real AR processing. It simply logs method calls
 * and acts as a placeholder until a proper ARCore-based solution is added.
 */
public class DummyARGrowthTracker implements ARGrowthTracker {

    private static final String TAG = "DummyARGrowthTracker";

    @Override
    public void init() {
        Timber.tag(TAG).d("init() called");
    }

    @Override
    public void trackGrowth(Bitmap currentView) {
        Timber.tag(TAG).d("trackGrowth() called with bitmap=" + currentView);
    }

    @Override
    public void cleanup() {
        Timber.tag(TAG).d("cleanup() called");
    }
}