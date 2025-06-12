package de.omagh.lumibuddy.data.model;

/**
 * Simple data holder for calibration profiles. A profile adjusts
 * lux readings from a specific source (ALS or Camera) by a factor
 * to improve PPFD accuracy.
 */
public class CalibrationProfile {
    public final String id;
    public final String name;
    public final String source; // e.g. "ALS" or "Camera"
    public final float calibrationFactor;
    public final String note;

    public CalibrationProfile(String id, String name, String source,
                              float calibrationFactor, String note) {
        this.id = id;
        this.name = name;
        this.source = source;
        this.calibrationFactor = calibrationFactor;
        this.note = note;
    }
}
