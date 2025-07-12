package de.omagh.core_infra.measurement;

import io.reactivex.rxjava3.core.Single;

/**
 * Placeholder CameraSpectralProvider that returns an error until a real
 * implementation is provided.
 */
public class StubCameraSpectralProvider implements CameraSpectralProvider {
    @Override
    public Single<float[]> captureRgb() {
        return Single.error(new UnsupportedOperationException("Camera provider not implemented"));
    }
}