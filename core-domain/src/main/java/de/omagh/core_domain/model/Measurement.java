package de.omagh.core_domain.model;

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
