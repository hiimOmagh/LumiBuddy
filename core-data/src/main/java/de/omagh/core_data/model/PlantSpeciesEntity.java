package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing basic plant species information fetched from remote APIs.
 */
@Entity(tableName = "plant_species")
public class PlantSpeciesEntity {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String scientificName;
    private final String commonName;
    private final String imageUrl;

    public PlantSpeciesEntity(@NonNull String id, String scientificName, String commonName, String imageUrl) {
        this.id = id;
        this.scientificName = scientificName;
        this.commonName = commonName;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}