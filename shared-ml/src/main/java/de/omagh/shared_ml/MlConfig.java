package de.omagh.shared_ml;

/**
 * Central configuration for on-device ML models.
 */
public final class MlConfig {
    private MlConfig() {}

    // Plant identification model configuration
    public static final String PLANT_MODEL = "plant_identifier.tflite";
    public static final String PLANT_LABELS = "plant_labels.txt";
    public static final int PLANT_INPUT_SIZE = 224;

    // Lamp identification model configuration
    public static final String LAMP_MODEL = "lamp_identifier.tflite";
    public static final String LAMP_LABELS = "lamp_labels.txt";
    public static final int LAMP_INPUT_SIZE = 224;
}
