package de.omagh.shared_ml;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Loads a TFLite model from the app's assets directory.
 */
public class AssetModelProvider implements ModelProvider {
    private final String assetPath;

    public AssetModelProvider(String assetPath) {
        this.assetPath = assetPath;
    }

    @Override
    public ByteBuffer loadModel(Context context) {
        try (InputStream is = context.getAssets().open(assetPath)) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int len;
            while ((len = is.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return ByteBuffer.wrap(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model from assets", e);
        }
    }
}