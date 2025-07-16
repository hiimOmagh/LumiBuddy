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
    @androidx.room.ColumnInfo(name = "updated_at")
    private final long updatedAt;
/*
    private final long updated;
*/

    public Plant(@NonNull String id, String name, String type, String imageUri) {
        this(id, name, type, imageUri, System.currentTimeMillis());
    }

    public Plant(@NonNull String id, String name, String type, String imageUri, long updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUri = imageUri;
        this.updatedAt = updatedAt;
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

    /*public long getUpdated() {
        return updated;
    }*/

    public long getUpdatedAt() {
        return updatedAt;
    }
}