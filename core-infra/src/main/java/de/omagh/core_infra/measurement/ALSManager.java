package de.omagh.core_infra.measurement;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Handles ambient light sensor (ALS) readings.
 * Calls OnLuxChangedListener on each sensor value update.
 */
public class ALSManager implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor lightSensor;
    private OnLuxChangedListener listener;

    public ALSManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    /**
     * Start listening for ALS updates.
     *
     * @param listener Callback to receive new lux values.
     */
    public void start(OnLuxChangedListener listener) {
        this.listener = listener;
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * Stop ALS updates.
     */
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float lux = event.values[0];
        if (listener != null) {
            listener.onLuxChanged(lux);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { /* not used */ }

    /**
     * Listener interface for reporting new lux values.
     */
    public interface OnLuxChangedListener {
        void onLuxChanged(float lux);
    }
}
