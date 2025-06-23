package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity storing detailed care information for a plant species and stage.
 */
@Entity(tableName = "plant_care_profiles")
public class PlantCareProfileEntity {
    @NonNull
    private final String speciesId;
    private final String stage;
    private final String sunlightRequirement;
    private final String soilType;
    private final int wateringIntervalDays;
    private final float minTemperature;
    private final float maxTemperature;
    private final float minHumidity;
    private final float maxHumidity;
    @PrimaryKey(autoGenerate = true)
    private long localId;

    public PlantCareProfileEntity(@NonNull String speciesId, String stage,
                                  String sunlightRequirement, String soilType,
                                  int wateringIntervalDays,
                                  float minTemperature, float maxTemperature,
                                  float minHumidity, float maxHumidity) {
        this.speciesId = speciesId;
        this.stage = stage;
        this.sunlightRequirement = sunlightRequirement;
        this.soilType = soilType;
        this.wateringIntervalDays = wateringIntervalDays;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHumidity = minHumidity;
        this.maxHumidity = maxHumidity;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    @NonNull
    public String getSpeciesId() {
        return speciesId;
    }

    public String getStage() {
        return stage;
    }

    public String getSunlightRequirement() {
        return sunlightRequirement;
    }

    public String getSoilType() {
        return soilType;
    }

    public int getWateringIntervalDays() {
        return wateringIntervalDays;
    }

    public float getMinTemperature() {
        return minTemperature;
    }

    public float getMaxTemperature() {
        return maxTemperature;
    }

    public float getMinHumidity() {
        return minHumidity;
    }

    public float getMaxHumidity() {
        return maxHumidity;
    }
}