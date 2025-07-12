package de.omagh.core_infra.measurement;

import static androidx.camera.lifecycle.ProcessCameraProvider.getInstance;

import android.app.Activity;

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

import timber.log.Timber;

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
        getInstance(activity).addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = getInstance(activity).get();
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
     * Analyze a single frame and compute a grid of light intensities along with mean RGB.
     */
    public void analyzeFrameWithGrid(int cols, int rows, @NonNull GridResultCallback callback) {
        if (imageAnalysis == null) {
            callback.onError("Camera not started");
            return;
        }
        if (analysisActive) return;
        analysisActive = true;

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), image -> {
            AnalysisResult result = computeGridRGB(image, cols, rows);
            image.close();
            analysisActive = false;
            imageAnalysis.clearAnalyzer();
            if (result != null) {
                callback.onResult(result.meanR, result.meanG, result.meanB, result.intensity);
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

    private AnalysisResult computeGridRGB(ImageProxy image, int cols, int rows) {
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

        float[][] rSum = new float[rows][cols];
        float[][] gSum = new float[rows][cols];
        float[][] bSum = new float[rows][cols];
        int[][] count = new int[rows][cols];

        long rTotal = 0, gTotal = 0, bTotal = 0;
        int totalCount = 0;

        for (int y = 0; y < height; y += 2) {
            for (int x = 0; x < width; x += 2) {
                int yIndex = y * yRowStride + x;
                int uvIndex = (y / 2) * uvRowStride + (x / 2) * uvPixelStride;

                int Y = yBuffer.get(yIndex) & 0xFF;
                int U = uBuffer.get(uvIndex) & 0xFF;
                int V = vBuffer.get(uvIndex) & 0xFF;

                float Yf = (float) Y;
                float Uf = (float) (U - 128);
                float Vf = (float) (V - 128);

                float R = Yf + 1.370705f * Vf;
                float G = Yf - 0.337633f * Uf - 0.698001f * Vf;
                float B = Yf + 1.732446f * Uf;

                int row = Math.min(rows - 1, (y * rows) / height);
                int col = Math.min(cols - 1, (x * cols) / width);
                rSum[row][col] += Math.max(0, Math.min(255, R));
                gSum[row][col] += Math.max(0, Math.min(255, G));
                bSum[row][col] += Math.max(0, Math.min(255, B));
                count[row][col]++;

                rTotal += Math.max(0, Math.min(255, (int) R));
                gTotal += Math.max(0, Math.min(255, (int) G));
                bTotal += Math.max(0, Math.min(255, (int) B));
                totalCount++;
            }
        }

        if (totalCount == 0) return null;

        float[][] intensity = new float[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (count[r][c] > 0) {
                    intensity[r][c] = (rSum[r][c] + gSum[r][c] + bSum[r][c]) / count[r][c];
                } else {
                    intensity[r][c] = 0f;
                }
            }
        }

        float meanR = rTotal / (float) totalCount;
        float meanG = gTotal / (float) totalCount;
        float meanB = bTotal / (float) totalCount;

        return new AnalysisResult(meanR, meanG, meanB, intensity);
    }

    public interface GridResultCallback {
        void onResult(float meanR, float meanG, float meanB, float[][] intensity);

        void onError(String message);
    }

    /**
     * Helper class to return grid results.
     */
    private static class AnalysisResult {
        final float meanR;
        final float meanG;
        final float meanB;
        final float[][] intensity;

        AnalysisResult(float meanR, float meanG, float meanB, float[][] intensity) {
            this.meanR = meanR;
            this.meanG = meanG;
            this.meanB = meanB;
            this.intensity = intensity;
        }
    }

    public interface ResultCallback {
        void onResult(float meanR, float meanG, float meanB);

        void onError(String message);
    }
}
