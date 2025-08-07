package de.omagh.shared_ml_cloud;

import android.content.Context;
import java.nio.ByteBuffer;

/**
 * Defines an API for retrieving machine learning models from a remote source.
 */
public interface CloudModelProvider {
    /**
     * Downloads the model identified by {@code modelId} and returns its contents.
     *
     * @param context Android context used for storage and networking
     * @param modelId unique identifier of the model
     * @return ByteBuffer containing the model data
     */
    ByteBuffer downloadModel(Context context, String modelId);
}
