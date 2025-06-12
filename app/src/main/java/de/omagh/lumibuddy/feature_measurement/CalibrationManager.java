package de.omagh.lumibuddy.feature_measurement;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles persistence of lux->PPFD calibration factors for each lamp profile.
 * Uses SharedPreferences for simplicity so it works without a full database.
 */
public class CalibrationManager {
    public static final float DEFAULT_FACTOR = 0.015f; // white LED baseline

    private static final String PREFS_NAME = "calibration_prefs";
    private static final String KEY_PREFIX = "factor_";

    private final SharedPreferences prefs;
    private final Map<String, Float> cache = new HashMap<>();

    public CalibrationManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the calibration factor for the given lamp profile ID.
     */
    public float getCalibrationFactor(String lampProfileId) {
        if (cache.containsKey(lampProfileId)) {
            Float v = cache.get(lampProfileId);
            return v;
        }
        float factor = prefs.getFloat(KEY_PREFIX + lampProfileId, DEFAULT_FACTOR);
        cache.put(lampProfileId, factor);
        return factor;
    }

    /**
     * Persists the calibration factor for the given lamp profile ID.
     */
    public void setCalibrationFactor(String lampProfileId, float factor) {
        cache.put(lampProfileId, factor);
        prefs.edit().putFloat(KEY_PREFIX + lampProfileId, factor).apply();
    }
}