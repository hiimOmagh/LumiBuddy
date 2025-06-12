package de.omagh.lumibuddy.feature_measurement;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

/**
 * Camera-based light meter. Takes a photo through a diffuser and analyzes mean RGB.
 */
public class CameraLightMeter {
    private final Activity activity;
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private ImageReader imageReader;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private CameraCallback callback;

    public interface CameraCallback {
        void onResult(float meanR, float meanG, float meanB);

        void onError(String error);
    }

    public CameraLightMeter(Activity activity) {
        this.activity = activity;
    }

    // Start background thread
    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraLightMeterBG");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    // Stop background thread
    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    // Initiate camera capture
    @RequiresPermission(Manifest.permission.CAMERA)
    public void capture(CameraCallback callback) {
        this.callback = callback;
        startBackgroundThread();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = manager.getCameraIdList()[0]; // Use the first camera (usually rear)
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = Objects.requireNonNull(map).getOutputSizes(ImageFormat.JPEG);
            Size selectedSize = sizes[0];

            imageReader = ImageReader.newInstance(selectedSize.getWidth(), selectedSize.getHeight(), ImageFormat.JPEG, 1);
            imageReader.setOnImageAvailableListener(reader -> {
                Image image = reader.acquireLatestImage();
                if (image != null) {
                    Bitmap bitmap = convertImageToBitmap(image);
                    image.close();
                    analyzeBitmap(bitmap);
                }
            }, backgroundHandler);

            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    try {
                        camera.createCaptureSession(
                                Collections.singletonList(imageReader.getSurface()),
                                new CameraCaptureSession.StateCallback() {
                                    @Override
                                    public void onConfigured(@NonNull CameraCaptureSession session) {
                                        captureSession = session;
                                        try {
                                            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                                            builder.addTarget(imageReader.getSurface());
                                            session.capture(builder.build(), null, backgroundHandler);
                                        } catch (CameraAccessException e) {
                                            callback.onError("Capture failed");
                                        }
                                    }

                                    @Override
                                    public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                                        callback.onError("Camera session config failed");
                                    }
                                },
                                backgroundHandler
                        );
                    } catch (CameraAccessException e) {
                        callback.onError("Failed to create capture session");
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    callback.onError("Camera error");
                }
            }, backgroundHandler);
        } catch (Exception e) {
            callback.onError("Camera open failed: " + e.getMessage());
        }
    }

    // Convert YUV Image to Bitmap (placeholder - implement JPEG decode as needed)
    private Bitmap convertImageToBitmap(Image image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // Analyze bitmap for mean R, G, B
    private void analyzeBitmap(Bitmap bitmap) {
        long rSum = 0, gSum = 0, bSum = 0;
        int w = bitmap.getWidth(), h = bitmap.getHeight(), n = w * h;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bitmap.getPixel(x, y);
                rSum += (color >> 16) & 0xFF;
                gSum += (color >> 8) & 0xFF;
                bSum += color & 0xFF;
            }
        }
        float meanR = rSum / (float) n;
        float meanG = gSum / (float) n;
        float meanB = bSum / (float) n;
        stopBackgroundThread();
        callback.onResult(meanR, meanG, meanB);
    }
}
