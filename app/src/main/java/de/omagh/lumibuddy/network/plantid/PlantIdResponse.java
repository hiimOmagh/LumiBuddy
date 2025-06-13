package de.omagh.lumibuddy.network.plantid;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response model for Plant.id identification results.
 */
public class PlantIdResponse {
    private List<Suggestion> suggestions;

    public List<Suggestion> getSuggestions() {
        return suggestions;
    }

    public static class Suggestion {
        @SerializedName("plant_name")
        private String plantName;
        @SerializedName("probability")
        private double probability;
        @SerializedName("plant_details")
        private PlantDetails plantDetails;

        public String getPlantName() {
            return plantName;
        }

        public double getProbability() {
            return probability;
        }

        public PlantDetails getPlantDetails() {
            return plantDetails;
        }
    }

    public static class PlantDetails {
        @SerializedName("common_names")
        private List<String> commonNames;

        public List<String> getCommonNames() {
            return commonNames;
        }
    }
}