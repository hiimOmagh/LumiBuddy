package de.omagh.lumibuddy.feature_plantdb;

import java.util.List;

/**
 * Repository wrapping {@link PlantDatabaseManager} and {@link PlantIdentifier} to
 * provide plant information and identification services for ViewModels.
 * All logic is synchronous and Java-only.
 */
public class PlantInfoRepository {
    private final PlantDatabaseManager dbManager;
    private final PlantIdentifier identifier;

    public PlantInfoRepository() {
        this.dbManager = new PlantDatabaseManager();
        this.identifier = new PlantIdentifier(dbManager);
    }

    /**
     * Returns all available sample plants.
     *
     * @return immutable list of plant info entries
     */
    public List<PlantInfo> getAllPlantInfo() {
        return dbManager.getAllPlants();
    }

    /**
     * Attempts to identify a plant by common or scientific name.
     *
     * @param name user provided query
     * @return matching PlantInfo or null if not found
     */
    public PlantInfo identifyPlant(String name) {
        return identifier.identifyByName(name);
    }
}