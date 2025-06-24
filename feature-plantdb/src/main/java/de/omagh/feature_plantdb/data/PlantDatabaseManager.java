package de.omagh.feature_plantdb.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.omagh.feature_plantdb.PlantCareProfile;
import de.omagh.feature_plantdb.PlantStage;

/**
 * Simple in-memory plant database used to provide example care profiles.
 * In the future this could be replaced by a Room database or remote API.
 */
public class PlantDatabaseManager {

    private final List<PlantInfo> plants = new ArrayList<>();

    public PlantDatabaseManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        // Tomato
        List<PlantCareProfile> tomatoProfiles = new ArrayList<>();
        tomatoProfiles.add(new PlantCareProfile(PlantStage.SEEDLING, 100, 200, 6, 12, 3, 60, 70, 20, 24));
        tomatoProfiles.add(new PlantCareProfile(PlantStage.VEGETATIVE, 200, 400, 12, 18, 2, 50, 70, 21, 27));
        tomatoProfiles.add(new PlantCareProfile(PlantStage.FLOWERING, 400, 600, 20, 30, 2, 50, 60, 22, 28));
        plants.add(new PlantInfo("Solanum lycopersicum", "Tomato", tomatoProfiles));

        // Basil
        List<PlantCareProfile> basilProfiles = new ArrayList<>();
        basilProfiles.add(new PlantCareProfile(PlantStage.SEEDLING, 100, 200, 6, 12, 3, 60, 70, 20, 24));
        basilProfiles.add(new PlantCareProfile(PlantStage.VEGETATIVE, 200, 300, 12, 18, 2, 60, 70, 20, 28));
        basilProfiles.add(new PlantCareProfile(PlantStage.FLOWERING, 300, 500, 20, 30, 2, 50, 60, 20, 28));
        plants.add(new PlantInfo("Ocimum basilicum", "Basil", basilProfiles));

        // Lettuce
        List<PlantCareProfile> lettuceProfiles = new ArrayList<>();
        lettuceProfiles.add(new PlantCareProfile(PlantStage.SEEDLING, 80, 150, 5, 10, 3, 60, 70, 18, 22));
        lettuceProfiles.add(new PlantCareProfile(PlantStage.VEGETATIVE, 150, 250, 10, 16, 2, 50, 70, 18, 24));
        lettuceProfiles.add(new PlantCareProfile(PlantStage.FLOWERING, 250, 400, 18, 25, 2, 50, 60, 20, 26));
        plants.add(new PlantInfo("Lactuca sativa", "Lettuce", lettuceProfiles));

        // Orchid
        List<PlantCareProfile> orchidProfiles = new ArrayList<>();
        orchidProfiles.add(new PlantCareProfile(PlantStage.SEEDLING, 50, 100, 4, 8, 7, 70, 80, 20, 26));
        orchidProfiles.add(new PlantCareProfile(PlantStage.VEGETATIVE, 100, 200, 8, 12, 5, 60, 80, 20, 28));
        orchidProfiles.add(new PlantCareProfile(PlantStage.FLOWERING, 150, 250, 10, 15, 5, 50, 70, 20, 28));
        plants.add(new PlantInfo("Phalaenopsis sp.", "Orchid", orchidProfiles));

        // Cactus
        List<PlantCareProfile> cactusProfiles = new ArrayList<>();
        cactusProfiles.add(new PlantCareProfile(PlantStage.SEEDLING, 100, 150, 8, 12, 14, 30, 50, 20, 30));
        cactusProfiles.add(new PlantCareProfile(PlantStage.VEGETATIVE, 250, 400, 15, 25, 14, 30, 50, 22, 32));
        cactusProfiles.add(new PlantCareProfile(PlantStage.FLOWERING, 400, 600, 20, 30, 14, 30, 50, 22, 32));
        plants.add(new PlantInfo("Echinopsis sp.", "Cactus", cactusProfiles));
    }

    /**
     * Returns an unmodifiable list of all sample plants.
     */
    public List<PlantInfo> getAllPlants() {
        return Collections.unmodifiableList(plants);
    }

    /**
     * Finds a plant by common or scientific name (case insensitive). Returns
     * null if no match is found.
     */
    public PlantInfo getPlantByName(String name) {
        if (name == null) return null;
        String lower = name.toLowerCase();
        for (PlantInfo plant : plants) {
            if (plant.commonName.toLowerCase().contains(lower) ||
                    plant.scientificName.toLowerCase().contains(lower)) {
                return plant;
            }
        }
        return null;
    }
}