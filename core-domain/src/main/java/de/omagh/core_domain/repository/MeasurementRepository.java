package de.omagh.core_domain.repository;

import io.reactivex.Observable;

/**
 * Abstraction for retrieving lux measurements from sensors.
 */
public interface MeasurementRepository {
    /**
     * Observe ambient light level in lux.
     */
    Observable<Float> observeLux();

    /**
     * Stop sensor updates when no longer needed.
     */
    void stopALS();
}