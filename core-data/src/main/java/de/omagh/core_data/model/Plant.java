package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a plant stored in the database.
 */
@Entity(tableName = "plants")
public class Plant {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String name;
    private final String type;
    private final String imageUri;

    public Plant(String id, String name, String type, String imageUri) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUri = imageUri;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getImageUri() {
        return imageUri;
    }
}