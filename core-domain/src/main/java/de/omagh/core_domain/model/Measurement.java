package de.omagh.core_domain.model;

/**
 * Represents a single light reading captured by the app.
 * <p>
 * Each measurement stores the raw lux value as well as derived
 * photosynthetic photon flux density (PPFD) and daily light integral (DLI).
 * The {@code timestamp} indicates when the reading was taken and
 * {@code source} identifies where the data came from (e.g. "ALS" or "Camera").
 */

/**
 * Represents a single light reading from either the ambient light sensor or an
 * image based estimate. The raw lux value can be converted to PPFD
 * (photosynthetic photon flux density) and DLI (daily light integral) using
 * calibration data.
 *
 * <p>
 * Important fields:
 * <ul>
 *   <li>{@code lux} - raw lux reading from the sensor</li>
 *   <li>{@code ppfd} - calibrated PPFD value derived from lux</li>
 *   <li>{@code dli} - calculated DLI for the day</li>
 *   <li>{@code timestamp} - epoch time when the reading was taken</li>
 *   <li>{@code source} - identifier such as {@code "ALS"} or {@code "Camera"}</li>
 * </ul>
 */
public class Measurement {
    public float lux;
    public float ppfd;
    public float dli;
    public long timestamp;
    public String source; // e.g., "ALS", "Camera"

    public Measurement() {
    }

    public Measurement(float lux, float ppfd, float dli, long timestamp, String source) {
        this.lux = lux;
        this.ppfd = ppfd;
        this.dli = dli;
        this.timestamp = timestamp;
        this.source = source;
    }
}
