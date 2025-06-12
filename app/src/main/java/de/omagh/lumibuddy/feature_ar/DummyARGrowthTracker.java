package de.omagh.lumibuddy.feature_ar;

import android.graphics.Bitmap;
import android.util.Log;

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
        Log.d(TAG, "init() called");
    }

    @Override
    public void trackGrowth(Bitmap currentView) {
        Log.d(TAG, "trackGrowth() called with bitmap=" + currentView);
    }

    @Override
    public void cleanup() {
        Log.d(TAG, "cleanup() called");
    }
}