package de.omagh.core_infra.network.plantid;

import java.util.List;

/**
 * Request body for Plant.id identification API.
 */
public class PlantIdRequest {
    private final List<String> images;

    public PlantIdRequest(List<String> images) {
        this.images = images;
    }

    public List<String> getImages() {
        return images;
    }
}