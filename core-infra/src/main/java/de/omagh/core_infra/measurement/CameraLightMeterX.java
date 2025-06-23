package de.omagh.core_infra.measurement;

import android.app.Activity;

import timber.log.Timber;

import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import org.jspecify.annotations.NonNull;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * CameraX helper for analyzing a single frame and reporting mean RGB.
 */
public class CameraLightMeterX {
    private final Activity activity;
    private final PreviewView previewView;
    private ImageAnalysis imageAnalysis;
    private ExecutorService cameraExecutor;
    private boolean analysisActive = false;

    public CameraLightMeterX(Activity activity, PreviewView previewView) {
        this.activity = activity;
        this.previewView = previewView;
    }

    /**
     * Start the camera preview with CameraX
     */
    public void startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor();
        ProcessCameraProvider.getInstance(activity).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = ProcessCameraProvider.getInstance(activity).get();
                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageAnalysis = new ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) activity,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview, imageAnalysis);

            } catch (Exception e) {
                Timber.tag("CameraLightMeterX").e(e, "Failed to start camera");
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    /**
     * Stop camera and release resources
     */
    public void stopCamera() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
            cameraExecutor = null;
        }
        if (imageAnalysis != null) {
            imageAnalysis.clearAnalyzer();
            imageAnalysis = null;
        }
    }

    /**
     * Analyze a single frame and call callback with mean RGB
     */
    public void analyzeFrame(@NonNull ResultCallback callback) {
        if (imageAnalysis == null) {
            callback.onError("Camera not started");
            return;
        }
        if (analysisActive) return;
        analysisActive = true;

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), image -> {
            float[] mean = computeMeanRGB(image);
            image.close();
            analysisActive = false;
            imageAnalysis.clearAnalyzer();
            if (mean != null) {
                callback.onResult(mean[0], mean[1], mean[2]);
            } else {
                callback.onError("Failed to analyze frame");
            }
        });
    }

    /**
     * Convert YUV_420_888 to mean R, G, B for the image.
     * Uses the standard color conversion formulas for each pixel.
     */
    private float[] computeMeanRGB(ImageProxy image) {
        int width = image.getWidth();
        int height = image.getHeight();
        ImageProxy.PlaneProxy yPlane = image.getPlanes()[0];
        ImageProxy.PlaneProxy uPlane = image.getPlanes()[1];
        ImageProxy.PlaneProxy vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        int yRowStride = yPlane.getRowStride();
        int uvRowStride = uPlane.getRowStride();
        int uvPixelStride = uPlane.getPixelStride();

        long rSum = 0, gSum = 0, bSum = 0;
        int pixelCount = 0;

        // Iterate over every other pixel for performance
        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {
                int yIndex = y * yRowStride + x;
                int uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride;

                int Y = yBuffer.get(yIndex) & 0xFF;
                int U = uBuffer.get(uvIndex) & 0xFF;
                int V = vBuffer.get(uvIndex) & 0xFF;

                // Convert YUV to RGB using formulas from Android docs
                float Yf = (float) Y;
                float Uf = (float) (U - 128);
                float Vf = (float) (V - 128);

                float R = Yf + 1.370705f * Vf;
                float G = Yf - 0.337633f * Uf - 0.698001f * Vf;
                float B = Yf + 1.732446f * Uf;

                rSum += Math.max(0, Math.min(255, (int) R));
                gSum += Math.max(0, Math.min(255, (int) G));
                bSum += Math.max(0, Math.min(255, (int) B));
                pixelCount++;
            }
        }

        if (pixelCount == 0) return null;
        float meanR = rSum / (float) pixelCount;
        float meanG = gSum / (float) pixelCount;
        float meanB = bSum / (float) pixelCount;

        return new float[]{meanR, meanG, meanB};
    }

    public interface ResultCallback {
        void onResult(float meanR, float meanG, float meanB);

        void onError(String message);
    }
}
