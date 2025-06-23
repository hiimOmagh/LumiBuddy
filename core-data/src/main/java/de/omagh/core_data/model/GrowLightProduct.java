package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Rich grow light product information fetched from external databases.
 */
@Entity(tableName = "grow_light_products")
public class GrowLightProduct {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String brand;
    private final String model;
    private final String spectrum;
    private final int wattage;
    private final float ppfd;
    private final String spectrumType;
    private final String certification;
    private final String imageUrl;

    public GrowLightProduct(@NonNull String id, String brand, String model,
                            String spectrum, int wattage, float ppfd,
                            String spectrumType, String certification,
                            String imageUrl) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.spectrum = spectrum;
        this.wattage = wattage;
        this.ppfd = ppfd;
        this.spectrumType = spectrumType;
        this.certification = certification;
        this.imageUrl = imageUrl;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public String getSpectrum() {
        return spectrum;
    }

    public int getWattage() {
        return wattage;
    }

    public float getPpfd() {
        return ppfd;
    }

    public String getSpectrumType() {
        return spectrumType;
    }

    public String getCertification() {
        return certification;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}