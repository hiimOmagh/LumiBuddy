package de.omagh.core_domain.model;

/**
 * Simple domain model representing a plant.
 */

public class Plant {
    private final String id;
    private final String name;
    private final String type;
    private final String imageUri;
    /*
        private final long updated;
    */
    private final long updatedAt;


    public Plant(String id, String name, String type, String imageUri) {
        this(id, name, type, imageUri, System.currentTimeMillis());
    }

    public Plant(String id, String name, String type, String imageUri, long updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.imageUri = imageUri;
        this.updatedAt = updatedAt;
    }

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

    public long getUpdatedAt() {
        return updatedAt;
    }
}
