package de.omagh.shared_ml;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import timber.log.Timber;

import de.omagh.shared_ml.ModelProvider;

/**
 * Simple on-device plant identifier backed by a TensorFlow Lite model.
 */
public class PlantIdentifier {
    private static final float DEFAULT_THRESHOLD = 0.7f;
    private final float threshold;

    private final Interpreter interpreter;
    private final ExecutorService executor;
    private final boolean ownsExecutor;
    private final String[] labels;
    private final ImageProcessor processor;
    private boolean closed = false;

    private String[] loadLabels(Context context, String assetPath) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(assetPath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (Exception e) {
            Timber.e(e, "Failed to load labels");
            return new String[]{"Unknown"};
        }
        return lines.toArray(new String[0]);
    }

    public PlantIdentifier(Context context, ModelProvider provider) {
        this(context, provider, "plant_labels.txt", DEFAULT_THRESHOLD);
    }

    public PlantIdentifier(Context context, ModelProvider provider, float threshold) {
        this(context, provider, "plant_labels.txt", threshold);
    }

    public PlantIdentifier(Context context, ModelProvider provider,
                           String labelAsset,
                           float threshold) {
        this(context, provider, Executors.newSingleThreadExecutor(), labelAsset, threshold, true);
    }

    public PlantIdentifier(Context context, ModelProvider provider,
                           ExecutorService executor,
                           String labelAsset,
                           float threshold) {
        this(context, provider, executor, labelAsset, threshold, false);
    }

    private PlantIdentifier(Context context, ModelProvider provider,
                            ExecutorService executor,
                            String labelAsset,
                            float threshold,
                            boolean ownsExecutor) {
        ByteBuffer model;
        try {
            model = provider.loadModel(context);
        } catch (Exception e) {
            Timber.e(e, "Failed to load plant model");
            throw new IllegalStateException("ML model unavailable", e);
        }
        interpreter = new Interpreter(model);
        this.executor = executor;
        this.ownsExecutor = ownsExecutor;
        this.threshold = threshold;
        this.labels = loadLabels(context, labelAsset);
        int inputSize = 224;
        processor = new ImageProcessor.Builder()
                .add(new ResizeOp(inputSize, inputSize, ResizeOp.ResizeMethod.BILINEAR))
                .add(new NormalizeOp(0f, 255f))
                .build();
    }

    /** Release model resources. */
    public void close() {
        closed = true;
        interpreter.close();
        if (ownsExecutor) {
            executor.shutdown();
        }
    }

    private List<Prediction> run(Bitmap bitmap) {
        TensorImage image = new TensorImage(DataType.FLOAT32);
        image.load(bitmap);
        image = processor.process(image);

        TensorBuffer output = TensorBuffer.createFixedSize(new int[] {1, labels.length}, DataType.FLOAT32);

        float[][] out = new float[1][labels.length];
        try {
            interpreter.run(image.getBuffer(), output.getBuffer());
            output.getBuffer().rewind();
            float[] arr = output.getFloatArray();
            System.arraycopy(arr, 0, out[0], 0, labels.length);
        } catch (Exception e) {
            Timber.e(e, "Inference failed");
            return null;
        }
        int[] idx = java.util.stream.IntStream.range(0, labels.length)
                .boxed()
                .sorted((a,b) -> Float.compare(out[0][b], out[0][a]))
                .mapToInt(Integer::intValue)
                .toArray();
        int topK = Math.min(3, labels.length);
        List<Prediction> preds = new ArrayList<>(topK);
        for (int i = 0; i < topK; i++) {
            preds.add(new Prediction(labels[idx[i]], out[0][idx[i]]));
        }
        return preds;
    }

    /**
     * Identify the plant in the given image asynchronously.
     */
    public LiveData<List<Prediction>> identifyPlant(Bitmap bitmap) {
        MutableLiveData<List<Prediction>> result = new MutableLiveData<>();
        executor.execute(() -> {
            if (closed) {
                result.postValue(null);
                return;
            }
            List<Prediction> preds = run(bitmap);
            if (preds == null) {
                Timber.e("Plant identifier returned null label");
                result.postValue(null);
                return;
            }
            result.postValue(preds);
        });
        return result;
    }

    public float getThreshold() {
        return threshold;
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
