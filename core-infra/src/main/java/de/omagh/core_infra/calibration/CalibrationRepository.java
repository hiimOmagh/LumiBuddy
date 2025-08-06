package de.omagh.core_infra.calibration;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Central storage for calibration factors and ML thresholds.
 */
public class CalibrationRepository {
    private static final String PREFS_NAME = "calibration_repo";
    private static final String KEY_DEVICE_PREFIX = "device_factor_";
    private static final String KEY_ML_THRESHOLD = "ml_threshold";
    private static final float DEFAULT_ML_THRESHOLD = 0.7f;
    private static final float DEFAULT_FACTOR = 0.015f;

    private final SharedPreferences prefs;
    private final Map<String, Float> deviceCache = new HashMap<>();

    public CalibrationRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public float getDefaultCalibrationFactor() {
        return DEFAULT_FACTOR;
    }

    public float getDeviceFactor(String source) {
        if (deviceCache.containsKey(source)) {
            return deviceCache.get(source);
        }
        float f = prefs.getFloat(KEY_DEVICE_PREFIX + source, 1f);
        deviceCache.put(source, f);
        return f;
    }

    public void setDeviceFactor(String source, float factor) {
        deviceCache.put(source, factor);
        prefs.edit().putFloat(KEY_DEVICE_PREFIX + source, factor).apply();
    }

    public float getMlThreshold() {
        return prefs.getFloat(KEY_ML_THRESHOLD, DEFAULT_ML_THRESHOLD);
    }

    public void setMlThreshold(float threshold) {
        prefs.edit().putFloat(KEY_ML_THRESHOLD, threshold).apply();
    }
}
