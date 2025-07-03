package de.omagh.core_infra.measurement;

/**
 * Simple data holder for grow light products.
 */
public class LampProduct {
    public final String id;
    public final String brand;
    public final String type; // e.g. LED, HPS
    public final String spectrum; // description of spectrum
    public final int wattage; // nominal wattage
    public final float calibrationFactor; // PPFD per lux
    public final float ppfdAt30cm; // example PPFD at 30cm if known
    public String name;

    public LampProduct(String id, String name, String brand, String type, String spectrum,
                       int wattage, float calibrationFactor, float ppfdAt30cm) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.type = type;
        this.spectrum = spectrum;
        this.wattage = wattage;
        this.calibrationFactor = calibrationFactor;
        this.ppfdAt30cm = ppfdAt30cm;
    }
}