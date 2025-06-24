package de.omagh.core_infra.network.plantid;

/**
 * Simplified representation of a Plant.id suggestion.
 */
public class PlantIdSuggestion {
    private final String commonName;
    private final String scientificName;

    public PlantIdSuggestion(String commonName, String scientificName) {
        this.commonName = commonName;
        this.scientificName = scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }
}