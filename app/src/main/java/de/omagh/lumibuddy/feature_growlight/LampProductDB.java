package de.omagh.lumibuddy.feature_growlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Very small in-memory database of common grow lights.
 * In the future this could be replaced with a Room DB or online service.
 */
public class LampProductDB {
    private final List<LampProduct> lamps = new ArrayList<>();

    public LampProductDB() {
        loadSampleData();
    }

    private void loadSampleData() {
        lamps.add(new LampProduct("SUN", "Sunlight", "Natural", "Full spectrum", 0.0185f, 2000));
        lamps.add(new LampProduct("LED1", "Generic White LED", "LED", "4000K", 0.019f, 800));
        lamps.add(new LampProduct("LED2", "Warm LED Bulb", "LED", "3000K", 0.021f, 700));
        lamps.add(new LampProduct("BLURPLE1", "Blurple Panel", "LED", "450nm/660nm", 0.045f, 900));
        lamps.add(new LampProduct("HPS1", "150W HPS", "HPS", "Yellow/Orange", 0.014f, 600));
        lamps.add(new LampProduct("CMH1", "315W CMH", "CMH", "4200K", 0.018f, 1100));
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
}