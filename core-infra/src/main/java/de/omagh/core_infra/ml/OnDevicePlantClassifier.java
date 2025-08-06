package de.omagh.core_infra.ml;

import android.content.Context;
import android.graphics.Bitmap;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.gpu.GpuDelegate;
import org.tensorflow.lite.nnapi.NnApiDelegate;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.shared_ml.AssetModelProvider;

/**
 * Very small on-device classifier using a bundled TFLite model.
 * This is a simplistic example and only returns "Known" or "Unknown".
 */
public class OnDevicePlantClassifier implements PlantClassifier {
    private final Interpreter interpreter;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String last;
    private boolean closed;
    private GpuDelegate gpuDelegate;
    private NnApiDelegate nnApiDelegate;

    public OnDevicePlantClassifier(Context context) {
        try {
            AssetModelProvider provider = new AssetModelProvider("plant_classifier.tflite");
            ByteBuffer model = provider.loadModel(context);

            Interpreter.Options options = new Interpreter.Options();
            options.setNumThreads(2);
            try {
                gpuDelegate = new GpuDelegate();
                options.addDelegate(gpuDelegate);
            } catch (Exception e) {
                try {
                    nnApiDelegate = new NnApiDelegate();
                    options.addDelegate(nnApiDelegate);
                } catch (Exception ignored) {
                    // Fall back to CPU
                }
            }

            interpreter = new Interpreter(model, options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Release model resources.
     */
    public void close() {
        closed = true;
        interpreter.close();
        if (gpuDelegate != null) {
            gpuDelegate.close();
        }
        if (nnApiDelegate != null) {
            nnApiDelegate.close();
        }
        executor.shutdown();
    }

    @Override
    public void classify(Bitmap bitmap) {
        executor.execute(() -> {
            int size = 224;
            TensorImage image = new TensorImage(DataType.FLOAT32);
            image.load(bitmap);
            ImageProcessor processor = new ImageProcessor.Builder()
                    .add(new ResizeOp(size, size, ResizeOp.ResizeMethod.BILINEAR))
                    .add(new NormalizeOp(0f, 255f))
                    .build();
            image = processor.process(image);
            if (closed) return;
            float[][] out = new float[1][1];
            interpreter.run(image.getBuffer(), out);
            last = out[0][0] > 0.5f ? "Known" : "Unknown";
        });
    }

    @Override
    public String getLastResult() {
        return last;
    }
}