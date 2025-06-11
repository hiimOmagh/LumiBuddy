package de.omagh.lumibuddy.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import androidx.annotation.NonNull;

@Entity(tableName = "plants")
public class Plant {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String name;
    private final String type;
    private final String imageUri; // <-- NEW

    public Plant(@NonNull String id, String name, String type, String imageUri) {
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
