package de.omagh.shared_ml_cloud;

import android.content.Context;
import java.nio.ByteBuffer;

/**
 * Placeholder implementation that does not download any models.
 */
public class NoOpCloudModelProvider implements CloudModelProvider {
    @Override
    public ByteBuffer downloadModel(Context context, String modelId) {
        throw new UnsupportedOperationException("Cloud model download not implemented");
    }
}
