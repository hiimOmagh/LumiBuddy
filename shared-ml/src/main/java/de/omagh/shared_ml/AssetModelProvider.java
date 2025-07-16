package de.omagh.shared_ml;

import android.content.Context;

import android.content.res.AssetFileDescriptor;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

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
        try (AssetFileDescriptor afd = context.getAssets().openFd(assetPath);
             FileInputStream fis = new FileInputStream(afd.getFileDescriptor());
             FileChannel channel = fis.getChannel()) {
            long startOffset = afd.getStartOffset();
            long declaredLength = afd.getDeclaredLength();
            return channel.map(FileChannel.MapMode.READ_ONLY,
                    startOffset, declaredLength);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load model from assets", e);
        }
    }
}
