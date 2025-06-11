package de.omagh.lumibuddy.feature_growlight;

/**
 * Simple data holder for grow light products.
 */
public class LampProduct {
    public final String id;
    public final String name;
    public final String type; // e.g. LED, HPS
    public final String spectrum; // description of spectrum
    public final float calibrationFactor; // PPFD per lux
    public final float ppfdAt30cm; // example PPFD at 30cm if known

    public LampProduct(String id, String name, String type, String spectrum,
                       float calibrationFactor, float ppfdAt30cm) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.spectrum = spectrum;
        this.calibrationFactor = calibrationFactor;
        this.ppfdAt30cm = ppfdAt30cm;
    }
}