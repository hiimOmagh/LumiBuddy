package de.omagh.core_infra.measurement;

import android.app.Activity;

import androidx.camera.view.PreviewView;

import io.reactivex.rxjava3.core.Single;

/**
 * Simple implementation using CameraLightMeterX to capture RGB.
 */
public class CameraSpectralProviderImpl implements CameraSpectralProvider {
    private final CameraLightMeterX meter;

    public CameraSpectralProviderImpl(Activity activity, PreviewView previewView) {
        this.meter = new CameraLightMeterX(activity, previewView);
    }

    @Override
    public Single<float[]> captureRgb() {
        return Single.create(emitter -> {
            meter.startCamera();
            meter.analyzeFrame(new CameraLightMeterX.ResultCallback() {
                @Override
                public void onResult(float meanR, float meanG, float meanB) {
                    emitter.onSuccess(new float[]{meanR, meanG, meanB});
                    meter.stopCamera();
                }

                @Override
                public void onError(String message) {
                    emitter.onError(new RuntimeException(message));
                    meter.stopCamera();
                }
            });
        });
    }
}