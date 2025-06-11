package de.omagh.lumibuddy.feature_growlight;

import android.graphics.Bitmap;

/**
 * Utility to identify lamps by name or in the future via photo/barcode.
 */
public class LampAutoIdentifier {
    private final LampProductDB db = new LampProductDB();

    /**
     * Simple name lookup (case-insensitive).
     */
    public LampProduct identifyLampByName(String name) {
        return db.findByName(name);
    }

    /**
     * Stub for photo-based identification.
     */
    public LampProduct identifyLampByPhoto(Bitmap image) {
        // TODO integrate ML or barcode scan
        return null;
    }
}