package de.omagh.core_domain.usecase;

import javax.inject.Inject;

import de.omagh.core_infra.measurement.MeasurementEngine;
import io.reactivex.Observable;

/**
 * Use case that exposes current lux measurements from the device's sensor.
 */
public class GetCurrentLuxUseCase {
    private final MeasurementEngine engine;

    @Inject
    public GetCurrentLuxUseCase(MeasurementEngine engine) {
        this.engine = engine;
    }

    /**
     * Observe lux readings.
     */
    public Observable<Float> execute() {
        return engine.observeLux();
    }

    public void stop() {
        engine.stopALS();
    }
}