package de.omagh.shared_ml;

import android.content.Context;

import java.nio.ByteBuffer;

/**
 * Provides the TensorFlow Lite model used by {@link PlantIdentifier}.
 */
public interface ModelProvider {
    /**
     * Loads the model from the given context.
     *
     * @param context Android context for accessing app resources
     * @return ByteBuffer containing the model
     */
    ByteBuffer loadModel(Context context);
}