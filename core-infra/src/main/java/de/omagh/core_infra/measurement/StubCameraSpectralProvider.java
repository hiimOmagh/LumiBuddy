package de.omagh.core_infra.measurement;

import io.reactivex.rxjava3.core.Single;

/**
 * Placeholder {@link CameraSpectralProvider} that returns an error until a real
 * implementation is provided. Useful for unit tests where camera access is not
 * available.
 */
public class StubCameraSpectralProvider implements CameraSpectralProvider {
    @Override
    public Single<float[]> captureRgb() {
        return Single.error(new UnsupportedOperationException("Camera provider not implemented"));
    }
}