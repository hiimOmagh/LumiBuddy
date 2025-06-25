package de.omagh.lumibuddy.feature_plantdb;

import android.graphics.Bitmap;

import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.plantdb.PlantInfo;

/**
 * Utility class for plant identification. Currently supports simple
 * name-based lookup and a stub for future photo identification.
 */
public class PlantIdentifier {

    private final PlantDatabaseManager dbManager;

    public PlantIdentifier(PlantDatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Attempts to identify a plant by its common or scientific name.
     * Matching is case-insensitive and may return null if nothing matches.
     */
    public PlantInfo identifyByName(String name) {
        return dbManager.getPlantByName(name);
    }

    /**
     * Stub for photo-based identification. Returns null for now.
     */
    public PlantInfo identifyByPhoto(Bitmap image) {
        // In the future use ML Kit or an online API to identify the plant
        return null;
    }
}