package de.omagh.lumibuddy.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Entity representing a grow light/lamp product.
 */
@Entity(tableName = "grow_light_profiles")
public class GrowLightProfile {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String name;
    private final String type;
    private final String spectrum;
    private final float calibrationFactor;
    private final float ppfdAt30cm;

    public GrowLightProfile(@NonNull String id, String name, String type, String spectrum,
                            float calibrationFactor, float ppfdAt30cm) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.spectrum = spectrum;
        this.calibrationFactor = calibrationFactor;
        this.ppfdAt30cm = ppfdAt30cm;
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

    public String getSpectrum() {
        return spectrum;
    }

    public float getCalibrationFactor() {
        return calibrationFactor;
    }

    public float getPpfdAt30cm() {
        return ppfdAt30cm;
    }
}