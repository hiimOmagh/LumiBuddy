package de.omagh.lumibuddy.feature_user;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Simple wrapper around {@link SharedPreferences} for storing user preferences.
 * <p>
 * Settings are persisted so that they survive app restarts until we
 * introduce a more sophisticated sync/profile system.
 */
public class SettingsManager {
    private static final String PREFS_NAME = "user_settings";
    private static final String KEY_UNIT = "preferred_unit";
    private static final String KEY_DURATION = "light_duration_hours";
    private static final String KEY_CALIB_ID = "selected_calibration_profile";
    private static final String KEY_ENABLE_AR = "enable_ar_overlay";
    private static final String KEY_ENABLE_ML = "enable_ml_features";
    private final SharedPreferences prefs;

    public SettingsManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the preferred measurement unit (Lux, PPFD or DLI).
     */
    public String getPreferredUnit() {
        return prefs.getString(KEY_UNIT, "Lux");
    }

    /**
     * Persist the preferred measurement unit.
     */
    public void setPreferredUnit(String unit) {
        prefs.edit().putString(KEY_UNIT, unit).apply();
    }

    /**
     * Returns the default daily light duration in hours used for DLI.
     */
    public int getLightDuration() {
        return prefs.getInt(KEY_DURATION, 24);
    }

    /**
     * Persist the default daily light duration in hours.
     */
    public void setLightDuration(int hours) {
        prefs.edit().putInt(KEY_DURATION, hours).apply();
    }

    /**
     * Returns the ID of the selected calibration/grow light profile.
     */
    public String getSelectedCalibrationProfileId() {
        return prefs.getString(KEY_CALIB_ID, "");
    }

    /**
     * Persist the selected calibration/grow light profile ID.
     */
    public void setSelectedCalibrationProfileId(String id) {
        prefs.edit().putString(KEY_CALIB_ID, id).apply();
    }

    /**
     * Returns true if AR overlay features are enabled.
     */
    public boolean isArOverlayEnabled() {
        return prefs.getBoolean(KEY_ENABLE_AR, false);
    }

    /**
     * Enable or disable AR overlay features.
     */
    public void setArOverlayEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLE_AR, enabled).apply();
    }

    /**
     * Returns true if ML features are enabled.
     */
    public boolean isMlFeaturesEnabled() {
        return prefs.getBoolean(KEY_ENABLE_ML, false);
    }

    /**
     * Enable or disable ML features.
     */
    public void setMlFeaturesEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_ENABLE_ML, enabled).apply();
    }
}