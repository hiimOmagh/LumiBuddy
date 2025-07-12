package de.omagh.core_infra.measurement;

import io.reactivex.rxjava3.core.Observable;

/**
 * Provides ambient light sensor readings as an Observable stream of lux values.
 */
public interface LightSensorProvider {
    /**
     * Observe ambient light levels in lux.
     */
    Observable<Float> observeLux();

    /**
     * Stop sensor updates.
     */
    void stop();
}