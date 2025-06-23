package de.omagh.core_domain.usecase;

import javax.inject.Inject;

import de.omagh.core_domain.repository.MeasurementRepository;
import io.reactivex.Observable;

/**
 * Use case that exposes current lux measurements from the device's sensor.
 */
public class GetCurrentLuxUseCase {
    private final MeasurementRepository repository;

    @Inject
    public GetCurrentLuxUseCase(MeasurementRepository repository) {
        this.repository = repository;
    }

    /**
     * Observe lux readings.
     */
    public Observable<Float> execute() {
        return repository.observeLux();
    }

    public void stop() {
        repository.stopALS();
    }
}