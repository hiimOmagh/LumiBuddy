package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Stores a correction factor for converting lux to PPFD for a given light type.
 */
@Entity(tableName = "light_corrections")
public class LightCorrectionEntity {
    @PrimaryKey
    @NonNull
    public final String type;
    public final float factor;

    public LightCorrectionEntity(@NonNull String type, float factor) {
        this.type = type;
        this.factor = factor;
    }
}
