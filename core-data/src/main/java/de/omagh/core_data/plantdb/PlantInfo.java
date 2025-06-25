package de.omagh.core_data.plantdb;

import java.util.List;

import de.omagh.core_domain.model.PlantCareProfile;
import de.omagh.core_domain.model.PlantStage;

/**
 * Basic information about a plant species along with its care profiles.
 */
public class PlantInfo {
    public final String scientificName;
    public final String commonName;
    public final List<PlantCareProfile> careProfiles;

    public PlantInfo(String scientificName, String commonName, List<PlantCareProfile> careProfiles) {
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.careProfiles = careProfiles;
    }

    /**
     * Returns the care profile matching the given stage, or null if not found.
     */
    public PlantCareProfile getProfileForStage(PlantStage stage) {
        for (PlantCareProfile profile : careProfiles) {
            if (profile.getStage() == stage) return profile;
        }
        return null;
    }
}