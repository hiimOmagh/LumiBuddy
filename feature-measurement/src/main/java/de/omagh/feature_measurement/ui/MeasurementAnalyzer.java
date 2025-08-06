package de.omagh.feature_measurement.ui;

import java.util.List;

import de.omagh.core_infra.measurement.LampProduct;

/**
 * Helper class for analyzing camera measurement results.
 */
public final class MeasurementAnalyzer {
    private MeasurementAnalyzer() {}

    /**
     * Attempts to auto-detect the lamp spectrum type based on mean RGB values.
     * Returns: [lampTypeString, warningStringOrNull]
     */
    public static String[] autoDetectLampType(float meanR, float meanG, float meanB) {
        float sum = meanR + meanG + meanB;
        if (sum == 0) return new String[]{"Unknown", "Sensor reading error."};
        float rRatio = meanR / sum;
        float gRatio = meanG / sum;
        float bRatio = meanB / sum;
        String type, warning = null;
        if (Math.abs(rRatio - gRatio) < 0.15f && Math.abs(gRatio - bRatio) < 0.15f) {
            type = "White/Sunlight";
        } else if (rRatio > 0.4f && bRatio > 0.4f && gRatio < 0.2f) {
            type = "Blurple LED";
            warning = "Warning: Unusual spectrum (purple/pink light).";
        } else if (gRatio > 0.5f && rRatio > 0.3f && bRatio < 0.1f) {
            type = "HPS";
            warning = "Warning: Unusual spectrum (yellow/orange).";
        } else if (bRatio > 0.6f && rRatio < 0.2f) {
            type = "Blue-dominant";
            warning = "Warning: High blue light.";
        } else if (rRatio > 0.6f && bRatio < 0.2f) {
            type = "Red-dominant";
            warning = "Warning: High red light.";
        } else {
            type = "Unknown/Other";
            warning = "Warning: Unusual or unknown spectrum.";
        }
        return new String[]{type, warning};
    }

    /**
     * Tries to match the suggested lamp type string to a lamp list index. Returns -1 if not found.
     */
    public static int lampTypeStringToIndex(List<LampProduct> lampList, String suggestion) {
        if (suggestion == null) return -1;
        String lower = suggestion.toLowerCase();
        for (int i = 0; i < lampList.size(); i++) {
            if (lampList.get(i).name.toLowerCase().contains(lower)) {
                return i;
            }
        }
        return -1;
    }
}
