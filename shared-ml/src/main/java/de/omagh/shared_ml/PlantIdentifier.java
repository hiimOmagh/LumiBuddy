package de.omagh.shared_ml;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;

import de.omagh.core_domain.util.AppExecutors;
import de.omagh.shared_ml.ModelProvider;

/**
 * Simple on-device plant identifier backed by a TensorFlow Lite model.
 */
public class PlantIdentifier {
    private final Interpreter interpreter;
    private final ExecutorService executor;
    private final int inputSize = 224;
    private final String[] labels = {"Unknown", "Plant"};

    public PlantIdentifier(Context context, ModelProvider provider, AppExecutors executors) {
        ByteBuffer model = provider.loadModel(context);
        interpreter = new Interpreter(model);
        this.executor = executors.single();
    }

    private String run(Bitmap bitmap) {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
        ByteBuffer buf = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4);
        buf.order(ByteOrder.nativeOrder());
        int[] pixels = new int[inputSize * inputSize];
        scaled.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize);
        int idx = 0;
        for (int i = 0; i < pixels.length; i++) {
            int val = pixels[i];
            buf.putFloat(((val >> 16) & 0xFF) / 255f);
            buf.putFloat(((val >> 8) & 0xFF) / 255f);
            buf.putFloat((val & 0xFF) / 255f);
        }
        float[][] out = new float[1][labels.length];
        interpreter.run(buf, out);
        int best = 0;
        float bestScore = -1f;
        for (int i = 0; i < labels.length; i++) {
            if (out[0][i] > bestScore) {
                bestScore = out[0][i];
                best = i;
            }
        }
        return labels[best];
    }

    /**
     * Identify the plant in the given image asynchronously.
     */
    public LiveData<String> identifyPlant(Bitmap bitmap) {
        MutableLiveData<String> result = new MutableLiveData<>();
        executor.execute(() -> result.postValue(run(bitmap)));
        return result;
    }
}