package de.omagh.core_infra.network.plantid;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response model for Plant.id identification results.
 */
public class PlantIdResponse {
    @SerializedName("model_version")
    private String modelVersion;

    private List<Suggestion> suggestions;

    public String getModelVersion() {
        return modelVersion;
    }

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public static class Suggestion {
        private String name;
        private double probability;

        @SerializedName("details")
        private PlantDetails plantDetails;

        @SerializedName("similar_images")
        private List<SimilarImage> similarImages;

        public String getName() {
            return name;
        }

        public double getProbability() {
            return probability;
        }

        public PlantDetails getPlantDetails() {
            return plantDetails;
        }

        public List<SimilarImage> getSimilarImages() {
            return similarImages;
        }
    }

    public static class PlantDetails {
        @SerializedName("common_names")
        private List<String> commonNames;

        public List<String> getCommonNames() {
            return commonNames;
        }
    }

    public static class SimilarImage {
        private String url;

        public String getUrl() {
            return url;
        }
    }
}
