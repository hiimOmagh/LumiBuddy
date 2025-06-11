package de.omagh.lumibuddy.feature_growlight;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the user's selected grow light profile and any custom lamp entries.
 */
public class GrowLightProfileManager {
    private static final String PREFS_NAME = "grow_light_profiles";
    private static final String KEY_ACTIVE = "active_id";
    private static final String KEY_CUSTOM = "custom_lamps";

    private final SharedPreferences prefs;
    private final LampProductDB productDB = new LampProductDB();
    private final List<LampProduct> customProfiles = new ArrayList<>();

    public GrowLightProfileManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadCustomProfiles();
    }

    /**
     * Returns all default and custom lamp profiles.
     */
    public List<LampProduct> getAllProfiles() {
        List<LampProduct> all = new ArrayList<>(productDB.getAll());
        all.addAll(customProfiles);
        return all;
    }

    /**
     * Returns the currently active lamp profile.
     */
    public LampProduct getActiveLampProfile() {
        String id = prefs.getString(KEY_ACTIVE, "SUN");
        LampProduct p = getById(id);
        return p != null ? p : productDB.getById("SUN");
    }

    /**
     * Set the active lamp profile by ID.
     */
    public void setActiveLampProfile(String id) {
        prefs.edit().putString(KEY_ACTIVE, id).apply();
    }

    /**
     * Find a lamp profile by ID across default and custom lists.
     */
    public LampProduct getById(String id) {
        for (LampProduct p : productDB.getAll()) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        for (LampProduct p : customProfiles) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    /**
     * Add a user-defined lamp profile and persist it.
     */
    public void addCustomProfile(LampProduct lamp) {
        customProfiles.add(lamp);
        saveCustomProfiles();
    }

    // --- Persistence helpers ---
    private void loadCustomProfiles() {
        String serialized = prefs.getString(KEY_CUSTOM, "");
        if (serialized == null || serialized.isEmpty()) return;
        String[] entries = serialized.split("\\|");
        for (String entry : entries) {
            String[] parts = entry.split(",");
            if (parts.length < 6) continue;
            try {
                customProfiles.add(new LampProduct(
                        parts[0], parts[1], parts[2], parts[3],
                        Float.parseFloat(parts[4]), Float.parseFloat(parts[5])));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void saveCustomProfiles() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < customProfiles.size(); i++) {
            LampProduct p = customProfiles.get(i);
            if (i > 0) sb.append("|");
            sb.append(p.id).append(",")
                    .append(p.name).append(",")
                    .append(p.type).append(",")
                    .append(p.spectrum).append(",")
                    .append(p.calibrationFactor).append(",")
                    .append(p.ppfdAt30cm);
        }
        prefs.edit().putString(KEY_CUSTOM, sb.toString()).apply();
    }
}