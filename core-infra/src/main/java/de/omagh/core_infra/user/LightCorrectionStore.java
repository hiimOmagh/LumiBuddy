package de.omagh.core_infra.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Persists per-light-type correction factors used to adjust lux to PPFD
 * conversion. Each light type (e.g. Sunlight, HPS) can have a device
 * specific multiplier stored in SharedPreferences.
 */
public class LightCorrectionStore {
    private static final String PREFS_NAME = "light_corrections";
    private static final String KEY_PREFIX = "factor_";

    private final SharedPreferences prefs;
    private final Map<String, Float> cache = new HashMap<>();

    public LightCorrectionStore(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the saved factor for the given light type or 1f if none set.
     */
    public float getFactor(String type) {
        if (cache.containsKey(type)) {
            return cache.get(type);
        }
        float f = prefs.getFloat(KEY_PREFIX + type, 1f);
        cache.put(type, f);
        return f;
    }

    /**
     * Persist a new factor for the given light type.
     */
    public void setFactor(String type, float factor) {
        cache.put(type, factor);
        prefs.edit().putFloat(KEY_PREFIX + type, factor).apply();
    }
}