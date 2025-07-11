package de.omagh.core_domain.model;

/**
 * Simple domain model representing a plant.
 */

public class Plant {
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
