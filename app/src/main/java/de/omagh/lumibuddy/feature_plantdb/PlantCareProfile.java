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
    // How often to water the plant in days
    private final int wateringIntervalDays;
    // Convenience targets used by the recommendation engine
    private final float targetDLI;
    private final float humidityRange;
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
            int wateringIntervalDays,
            float minHumidity,
            float maxHumidity,
            float minTemperature,
            float maxTemperature) {
        this.stage = stage;
        this.minPPFD = minPPFD;
        this.maxPPFD = maxPPFD;
        this.minDLI = minDLI;
        this.maxDLI = maxDLI;
        this.wateringIntervalDays = wateringIntervalDays;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.targetDLI = (minDLI + maxDLI) / 2f;
        this.humidityRange = maxHumidity - minHumidity;
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

    public float getTargetDLI() {
        return targetDLI;
    }

    public float getHumidityRange() {
        return humidityRange;
    }

    /**
     * Alias for backwards compatibility.
     */
    public int getWaterFrequencyDays() {
        return wateringIntervalDays;
    }

    public int getWateringIntervalDays() {
        return wateringIntervalDays;
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