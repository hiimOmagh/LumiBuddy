package de.omagh.lumibuddy.feature_plantdb;

/**
 * Care parameters for a plant at a specific growth stage.
 * Values are deliberately simple so they can be expanded later
 * or persisted in a database.
 */
public class PlantCareProfile {
    private final PlantStage stage;
    private final float minPPFD;
    private final float maxPPFD;
    private final float minDLI;
    private final float maxDLI;
    private final int waterFrequencyDays;
    private final float minHumidity;
    private final float maxHumidity;
    private final float minTemperature;
    private final float maxTemperature;

    public PlantCareProfile(
            PlantStage stage,
            float minPPFD,
            float maxPPFD,
            float minDLI,
            float maxDLI,
            int waterFrequencyDays,
            float minHumidity,
            float maxHumidity,
            float minTemperature,
            float maxTemperature) {
        this.stage = stage;
        this.minPPFD = minPPFD;
        this.maxPPFD = maxPPFD;
        this.minDLI = minDLI;
        this.maxDLI = maxDLI;
        this.waterFrequencyDays = waterFrequencyDays;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
    }

    public PlantStage getStage() {
        return stage;
    }

    public float getMinPPFD() {
        return minPPFD;
    }

    public float getMaxPPFD() {
        return maxPPFD;
    }

    public float getMinDLI() {
        return minDLI;
    }

    public float getMaxDLI() {
        return maxDLI;
    }

    public int getWaterFrequencyDays() {
        return waterFrequencyDays;
    }

    public float getMinHumidity() {
        return minHumidity;
    }

    public float getMaxHumidity() {
        return maxHumidity;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }
}