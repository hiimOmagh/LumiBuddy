package de.omagh.core_domain.model;

/**
 * Domain model representing basic plant species information.
 */
public class PlantSpecies {
    private final String id;
    private final String scientificName;
    private final String commonName;
    private final String imageUrl;

    public PlantSpecies(String id, String scientificName, String commonName, String imageUrl) {
        this.id = id;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public String getScientificName() { return scientificName; }
    public String getCommonName() { return commonName; }
    public String getImageUrl() { return imageUrl; }
}
