package de.omagh.core_infra.environment;

import android.Manifest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.RequiresPermission;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

/**
 * Estimates daily sunlight hours based on device location and orientation.
 */
public class SunlightEstimator {
    private final LocationManager locationManager;
    private final SensorManager sensorManager;

    @Inject
    public SunlightEstimator(LocationManager locationManager, SensorManager sensorManager) {
        this.locationManager = locationManager;
        this.sensorManager = sensorManager;
    }

    /**
     * Returns the estimated hours of direct sunlight for the current location
     * and device orientation. Values are clamped to the range 1-24.
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public int estimateDailySunlightHours() {
        float latitude = 0f;
        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) latitude = (float) loc.getLatitude();
        } catch (SecurityException ignored) {
        }

        float base = 12f + 4f * (float) Math.cos(Math.toRadians(latitude));
        float orientationFactor = getSouthFacingFactor();
        int hours = Math.round(base * orientationFactor);
        if (hours < 1) hours = 1;
        if (hours > 24) hours = 24;
        return hours;
    }

    private float getSouthFacingFactor() {
        Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (rotation == null) return 1f;
        final float[] az = new float[1];
        final CountDownLatch latch = new CountDownLatch(1);
        SensorEventListener listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] r = new float[9];
                SensorManager.getRotationMatrixFromVector(r, event.values);
                float[] o = new float[3];
                SensorManager.getOrientation(r, o);
                az[0] = o[0];
                latch.countDown();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };
        sensorManager.registerListener(listener, rotation, SensorManager.SENSOR_DELAY_UI);
        try {
            latch.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ignored) {
        }
        sensorManager.unregisterListener(listener);
        float deviation = Math.abs(az[0] - (float) Math.toRadians(180));
        float factor = (float) Math.cos(deviation);
        return Math.max(0.2f, Math.abs(factor));
    }
}
