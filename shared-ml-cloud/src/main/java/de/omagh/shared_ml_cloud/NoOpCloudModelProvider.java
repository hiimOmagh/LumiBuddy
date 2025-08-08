package de.omagh.shared_ml_cloud;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.TimeUnit;

/**
 * Retrieves machine learning models from a remote HTTP endpoint and caches them locally.
 * <p>
 * Models are cached in the application's cache directory using the {@code modelId} as the
 * filename. If a model is already cached it will be loaded from disk instead of performing a
 * network request.
 * </p>
 */
public class NoOpCloudModelProvider implements CloudModelProvider {

    /** Base URL where models can be downloaded from. */
    private static final String BASE_URL = "https://example.com/models/"; // TODO: replace with your own URL

    private static final int CONNECT_TIMEOUT_MS = (int) TimeUnit.SECONDS.toMillis(15);
    private static final int READ_TIMEOUT_MS = (int) TimeUnit.SECONDS.toMillis(30);

    @Override
    public ByteBuffer downloadModel(Context context, String modelId) {
        File cacheFile = new File(context.getCacheDir(), modelId);

        // Return the cached model if it exists
        if (cacheFile.exists()) {
            try {
                return readFile(cacheFile);
            } catch (IOException e) {
                throw new ModelDownloadException("Failed to read cached model: " + modelId, e);
            }
        }

        // Otherwise download the model and cache it
        HttpURLConnection connection = null;
        try {
            URL url = buildUrl(modelId);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Unexpected HTTP response: " + responseCode);
            }

            try (InputStream in = new BufferedInputStream(connection.getInputStream());
                 OutputStream out = new FileOutputStream(cacheFile)) {
                byte[] buffer = new byte[8 * 1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }

            return readFile(cacheFile);
        } catch (IOException e) {
            if (cacheFile.exists()) {
                // Ensure incomplete files are not left behind
                cacheFile.delete();
            }
            throw new ModelDownloadException("Failed to download model: " + modelId, e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static URL buildUrl(String modelId) throws MalformedURLException {
        return new URL(BASE_URL + Uri.encode(modelId));
    }

    private static ByteBuffer readFile(File file) throws IOException {
        try (FileInputStream input = new FileInputStream(file);
             FileChannel channel = input.getChannel()) {
            return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
        }
    }

    /**
     * Exception thrown when a model could not be downloaded or read from the cache.
     */
    static class ModelDownloadException extends RuntimeException {
        ModelDownloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
