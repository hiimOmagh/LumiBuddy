package de.omagh.lumibuddy.feature_measurement;

/**
 * Utility methods for light measurement conversions.
 */
public class MeasurementUtils {
    /**
     * Converts lux to PPFD using a calibration factor.
     */
    public static float luxToPPFD(float lux, float factor) {
        return lux * factor;
    }

    /**
     * Converts PPFD and hours to DLI.
     */
    public static float ppfdToDLI(float ppfd, int hours) {
        return (ppfd * hours * 3600f) / 1_000_000f;
    }
}