package de.omagh.core_infra.util;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Utility helpers for working with images.
 */
public class ImageUtils {
    private ImageUtils() {
        // no instance
    }

    /**
     * Copies the given image URI into the app's internal storage directory so it
     * remains accessible even if the original source goes away. Returns the
     * resulting file {@link Uri} for persistent storage.
     *
     * @param context Context used to access the content resolver
     * @param uri     Source image URI selected by the user
     * @return Uri to the copied file in internal storage
     */
    public static Uri copyUriToInternalStorage(Context context, Uri uri) {
        if (context == null || uri == null) return uri;
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            if (in == null) return uri;
            String ext = getExtension(context, uri);
            File dir = new File(context.getFilesDir(), "images");
            if (!dir.exists()) {
                //noinspection ResultOfMethodCallIgnored
                dir.mkdirs();
            }
            File outFile = File.createTempFile("img_", ext != null ? "." + ext : "", dir);
            try (OutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.flush();
            }
            return Uri.fromFile(outFile);
        } catch (IOException e) {
            e.printStackTrace();
            return uri;
        }
    }

    private static String getExtension(Context context, Uri uri) {
        String ext = null;
        String type = context.getContentResolver().getType(uri);
        if (type != null) {
            ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
        }
        if (ext == null) {
            String path = uri.getPath();
            if (path != null) {
                int dot = path.lastIndexOf('.');
                if (dot >= 0) ext = path.substring(dot + 1).toLowerCase(Locale.US);
            }
        }
        return ext;
    }
}
