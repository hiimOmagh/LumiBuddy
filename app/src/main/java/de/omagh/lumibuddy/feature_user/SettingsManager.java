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
}