package de.omagh.lumibuddy.feature_ar;

import android.graphics.Canvas;

import timber.log.Timber;

import de.omagh.core_domain.model.Measurement;

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
        Timber.tag(TAG).d("init() called");
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement) {
        Timber.tag(TAG).d("renderOverlay() called with measurement=" + measurement);
    }

    @Override
    public void cleanup() {
        Timber.tag(TAG).d("cleanup() called");
    }
}