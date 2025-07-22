package de.omagh.core_infra.ml;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.Interpreter;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Very small on-device classifier using a bundled TFLite model.
 * This is a simplistic example and only returns "Known" or "Unknown".
 */
public class OnDevicePlantClassifier implements PlantClassifier {
    private final Interpreter interpreter;
    private String last;
    private boolean closed;

    public OnDevicePlantClassifier(Context context) {
        try {
            InputStream is = context.getAssets().open("plant_classifier.tflite");
            byte[] model = new byte[is.available()];
            int r = is.read(model);
            is.close();
            interpreter = new Interpreter(ByteBuffer.wrap(model));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /** Release model resources. */
    public void close() {
        closed = true;
        interpreter.close();
    }
    
    @Override
    public void classify(Bitmap bitmap) {
        int size = 224;
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, size, size, true);
        ByteBuffer buf = ByteBuffer.allocateDirect(size * size * 3 * 4);
        buf.order(ByteOrder.nativeOrder());
        int[] values = new int[size * size];
        scaled.getPixels(values, 0, size, 0, 0, size, size);
        int idx = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int val = values[idx++];
                buf.putFloat(((val >> 16) & 0xFF) / 255f);
                buf.putFloat(((val >> 8) & 0xFF) / 255f);
                buf.putFloat((val & 0xFF) / 255f);
            }
        }
        if (closed) return;
        float[][] out = new float[1][1];
        interpreter.run(buf, out);
        last = out[0][0] > 0.5f ? "Known" : "Unknown";
    }

    @Override
    public String getLastResult() {
        return last;
    }
}