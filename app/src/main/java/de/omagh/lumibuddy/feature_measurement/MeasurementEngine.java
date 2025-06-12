package de.omagh.lumibuddy.feature_measurement;

import android.content.Context;

/**
 * Orchestrates measurement sources (ALS, Camera, etc).
 * Currently wraps ALSManager for live light readings.
 */
public class MeasurementEngine {
    private final ALSManager alsManager;

    public MeasurementEngine(Context context) {
        alsManager = new ALSManager(context);
    }

    /**
     * Start ALS measurement and receive results via callback.
     *
     * @param listener Callback for new lux values.
     */
    public void startALS(ALSManager.OnLuxChangedListener listener) {
        alsManager.start(listener);
    }

    /**
     * Stop ALS measurement.
     */
    public void stopALS() {
        alsManager.stop();
    }
}
