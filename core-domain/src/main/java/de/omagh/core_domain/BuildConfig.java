package de.omagh.core_domain;

/**
 * Placeholder BuildConfig to allow modules without the Android Gradle Plugin
 * to reference configuration constants. Android modules generate their own
 * BuildConfig at compile time so this class is ignored there.
 *
 * <p>Keep values empty in source control.</p>
*/
public final class BuildConfig {
    /**
     * API key for Plant.id if provided via Gradle.
     */
    public static final String PLANT_ID_API_KEY = "";

    private BuildConfig() {
    }
}