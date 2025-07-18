package de.omagh.feature_measurement.infra;

import android.graphics.Bitmap;

import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.measurement.LampProductDB;

/**
 * Utility to identify lamps by name or in the future via photo/barcode.
 */
public class LampAutoIdentifier {
    private final LampProductDB db;

    public LampAutoIdentifier(android.content.Context context) {
        this.db = new LampProductDB(context);
    }

    /**
     * Simple name lookup (case-insensitive).
     */
    public LampProduct identifyLampByName(String name) {
        return db.findByName(name);
    }

    /**
     * Stub for photo-based identification. Currently returns {@code null}.
     * Replace with ML model or barcode scanning logic in the future.
     */
    public LampProduct identifyLampByPhoto(Bitmap image) {
        return null;
    }
}