package de.omagh.core_infra.measurement;

import io.reactivex.rxjava3.core.Single;

/**
 * Provides a single capture of mean RGB values using the device camera.
 */
public interface CameraSpectralProvider {
    /**
     * Capture one frame and return mean R,G,B values.
     */
    Single<float[]> captureRgb();
}