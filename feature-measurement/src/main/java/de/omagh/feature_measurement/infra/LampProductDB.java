package de.omagh.feature_measurement.infra;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Very small in-memory database of common grow lights.
 * In the future this could be replaced with a Room DB or online service.
 */
public class LampProductDB {
    private final List<LampProduct> lamps = new ArrayList<>();

    private final Context context;

    public LampProductDB(Context ctx) {
        this.context = ctx.getApplicationContext();
        loadSampleData();
    }

    private void loadSampleData() {
        try {
            AssetManager am = context.getAssets();
            InputStream is = am.open("grow_lights.json");
            byte[] buf = new byte[is.available()];
            int read = is.read(buf);
            is.close();
            if (read <= 0) return;
            String json = new String(buf);
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                lamps.add(new LampProduct(
                        o.getString("id"),
                        o.getString("name"),
                        o.optString("brand"),
                        o.getString("type"),
                        o.getString("spectrum"),
                        o.optInt("wattage"),
                        (float) o.optDouble("calibrationFactor"),
                        (float) o.optDouble("ppfdAt30cm")));
            }
        } catch (IOException | org.json.JSONException ignored) {
        }
    }

    /**
     * Returns all lamp products.
     */
    public List<LampProduct> getAll() {
        return Collections.unmodifiableList(lamps);
    }

    /**
     * Finds a lamp by its ID.
     */
    public LampProduct getById(String id) {
        for (LampProduct p : lamps) {
            if (p.id.equalsIgnoreCase(id)) return p;
        }
        return null;
    }

    /**
     * Case-insensitive search by name; returns first match or null.
     */
    public LampProduct findByName(String name) {
        String lower = name.toLowerCase();
        for (LampProduct p : lamps) {
            if (p.name.toLowerCase().contains(lower)) return p;
        }
        return null;
    }

    /**
     * Case-insensitive search by brand.
     */
    public LampProduct findByBrand(String brand) {
        String lower = brand.toLowerCase();
        for (LampProduct p : lamps) {
            if (p.brand != null && p.brand.toLowerCase().contains(lower)) return p;
        }
        return null;
    }

    /**
     * Case-insensitive search by spectrum.
     */
    public LampProduct findBySpectrum(String spectrum) {
        String lower = spectrum.toLowerCase();
        for (LampProduct p : lamps) {
            if (p.spectrum.toLowerCase().contains(lower)) return p;
        }
        return null;
    }
}