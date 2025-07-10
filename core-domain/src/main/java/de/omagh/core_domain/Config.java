package de.omagh.core_domain;

/**
 * Application-wide configuration constants shared across modules.
 */
public class Config {
    /**
     * Base URL for network requests.
     */
    public static final String BASE_URL = "https://your.backend.url/";

    /**
     * API key for Plant.id identification service. The value is provided via
     * BuildConfig to allow injection from Gradle or local.properties.
     */
    public static final String PLANT_ID_API_KEY = BuildConfig.PLANT_ID_API_KEY;
}