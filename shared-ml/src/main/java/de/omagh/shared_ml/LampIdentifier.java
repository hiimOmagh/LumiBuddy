package de.omagh.shared_ml;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.tensorflow.lite.Interpreter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.ExecutorService;
import timber.log.Timber;

import de.omagh.core_domain.util.AppExecutors;

/**
 * Simple on-device lamp identifier backed by a TensorFlow Lite model.
 */
public class LampIdentifier {
    private static final float DEFAULT_THRESHOLD = 0.7f;
    private final float threshold;

    private final Interpreter interpreter;
    private final ExecutorService executor;
    private final String[] labels = {"Unknown", "Lamp"};
    private final ByteBuffer inputBuffer;

    public LampIdentifier(Context context, ModelProvider provider, AppExecutors executors) {
        this(context, provider, executors, DEFAULT_THRESHOLD);
    }

    public LampIdentifier(Context context, ModelProvider provider, AppExecutors executors, float threshold) {
        ByteBuffer model;
        try {
            model = provider.loadModel(context);
        } catch (Exception e) {
            Timber.e(e, "Failed to load lamp model");
            throw new IllegalStateException("ML model unavailable", e);
        }
        interpreter = new Interpreter(model);
        this.executor = executors.single();
        this.threshold = threshold;
        int inputSize = 224;
        inputBuffer = ByteBuffer.allocateDirect(inputSize * inputSize * 3 * 4);
        inputBuffer.order(ByteOrder.nativeOrder());
    }

    private Prediction run(Bitmap bitmap) {
        int inputSize = 224;
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
        inputBuffer.rewind();
        int[] pixels = new int[inputSize * inputSize];
        scaled.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize);
        for (int val : pixels) {
            inputBuffer.putFloat(((val >> 16) & 0xFF) / 255f);
            inputBuffer.putFloat(((val >> 8) & 0xFF) / 255f);
            inputBuffer.putFloat((val & 0xFF) / 255f);
        }
        float[][] out = new float[1][labels.length];
        try {
            interpreter.run(inputBuffer, out);
        } catch (Exception e) {
            Timber.e(e, "Inference failed");
            return new Prediction(null, 0f);
        }
        int best = 0;
        float bestScore = -1f;
        for (int i = 0; i < labels.length; i++) {
            if (out[0][i] > bestScore) {
                bestScore = out[0][i];
                best = i;
            }
        }
        return new Prediction(labels[best], bestScore);
    }

    /**
     * Identify the lamp in the given image asynchronously.
     */
    public LiveData<Prediction> identifyLamp(Bitmap bitmap) {
        MutableLiveData<Prediction> result = new MutableLiveData<>();
        executor.execute(() -> {
            Prediction pred = run(bitmap);
            if (pred.getLabel() == null) {
                Timber.e("Lamp identifier returned null label");
                result.postValue(null);
                return;
            }
            if (pred.getConfidence() < threshold) {
                result.postValue(new Prediction(null, pred.getConfidence()));
            } else {
                result.postValue(pred);
            }
        });
        return result;
    }

    public static class Prediction {
        private final String label;
        private final float confidence;

        public Prediction(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }

        public String getLabel() {
            return label;
        }

        public float getConfidence() {
            return confidence;
        }
    }
}
