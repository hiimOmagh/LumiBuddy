package de.omagh.core_infra.user;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.omagh.core_domain.model.CalibrationProfile;

/**
 * Manages user-defined calibration profiles. The implementation uses
 * SharedPreferences so it can easily be replaced with a database or
 * cloud sync in the future.
 */
public class CalibrationProfilesManager {
    private static final String PREFS_NAME = "calibration_profiles";
    private static final String KEY_ACTIVE = "active_profile_id";
    private static final String KEY_LIST = "profiles";

    private final SharedPreferences prefs;
    private final List<CalibrationProfile> profiles = new ArrayList<>();

    public CalibrationProfilesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        loadProfiles();
    }

    // --- Profile CRUD ---
    public List<CalibrationProfile> getProfiles() {
        return Collections.unmodifiableList(profiles);
    }

    public void addProfile(CalibrationProfile p) {
        profiles.add(p);
        saveProfiles();
    }

    public void updateProfile(CalibrationProfile p) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id.equals(p.id)) {
                profiles.set(i, p);
                saveProfiles();
                break;
            }
        }
    }

    public void removeProfile(String id) {
        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).id.equals(id)) {
                profiles.remove(i);
                saveProfiles();
                break;
            }
        }
        if (id.equals(getActiveProfileId())) {
            setActiveProfile(null);
        }
    }

    // --- Active profile selection ---
    public CalibrationProfile getActiveProfile() {
        String id = getActiveProfileId();
        if (id == null) return null;
        for (CalibrationProfile p : profiles) {
            if (p.id.equals(id)) return p;
        }
        return null;
    }

    public void setActiveProfile(String id) {
        if (id == null) {
            prefs.edit().remove(KEY_ACTIVE).apply();
        } else {
            prefs.edit().putString(KEY_ACTIVE, id).apply();
        }
    }

    public float getCalibrationFactorForSource(String source) {
        CalibrationProfile p = getActiveProfile();
        if (p != null && p.source.equalsIgnoreCase(source)) {
            return p.calibrationFactor;
        }
        return 1f;
    }

    // --- Persistence helpers ---
    private String getActiveProfileId() {
        return prefs.getString(KEY_ACTIVE, null);
    }

    private void loadProfiles() {
        profiles.clear();
        String serialized = prefs.getString(KEY_LIST, "");
        if (serialized.isEmpty()) return;
        String[] entries = serialized.split("\\|");
        for (String entry : entries) {
            String[] parts = entry.split(",", -1);
            if (parts.length < 4) continue;
            try {
                String id = parts[0];
                String name = parts[1];
                String source = parts[2];
                float factor = Float.parseFloat(parts[3]);
                String note = parts.length > 4 ? parts[4] : "";
                profiles.add(new CalibrationProfile(id, name, source, factor, note));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void saveProfiles() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < profiles.size(); i++) {
            CalibrationProfile p = profiles.get(i);
            if (i > 0) sb.append('|');
            sb.append(p.id).append(',')
                    .append(p.name).append(',')
                    .append(p.source).append(',')
                    .append(p.calibrationFactor).append(',')
                    .append(p.note == null ? "" : p.note.replace("|", " ").replace(",", " "));
        }
        prefs.edit().putString(KEY_LIST, sb.toString()).apply();
    }
}