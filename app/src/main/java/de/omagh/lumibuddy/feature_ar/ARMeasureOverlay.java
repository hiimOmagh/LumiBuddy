package de.omagh.lumibuddy.feature_ar;

import android.graphics.Canvas;
import android.util.Log;

import de.omagh.lumibuddy.data.model.Measurement;

/**
 * Stub implementation of {@link AROverlayRenderer}.
 * <p>
 * This class does not perform any real AR rendering yet. It simply logs
 * the calls it receives and acts as an integration point for a future
 * ARCore based overlay renderer.
 */
public class ARMeasureOverlay implements AROverlayRenderer {

    private static final String TAG = "ARMeasureOverlay";

    @Override
    public void init() {
        Log.d(TAG, "init() called");
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement) {
        Log.d(TAG, "renderOverlay() called with measurement=" + measurement);
    }

    @Override
    public void cleanup() {
        Log.d(TAG, "cleanup() called");
    }
}