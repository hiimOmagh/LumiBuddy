package de.omagh.core_infra.network.plantid;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Request body for Plant.id identification API.
 */
public class PlantIdRequest {
    private final List<String> images;

    @SerializedName("similar_images")
    @Nullable
    private final Boolean similarImages;
    public PlantIdRequest(List<String> images) {
        this(images, null);
    }

    public PlantIdRequest(List<String> images, @Nullable Boolean similarImages) {
        this.images = images;
        this.similarImages = similarImages;
    }

    public List<String> getImages() {
        return images;
    }

    @Nullable
    public Boolean getSimilarImages() {
        return similarImages;
    }
}
