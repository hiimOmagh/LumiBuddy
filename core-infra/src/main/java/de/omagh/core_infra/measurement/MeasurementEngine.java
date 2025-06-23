package de.omagh.core_infra.measurement;

import io.reactivex.Observable;
import de.omagh.core_domain.repository.MeasurementRepository;

/**
 * Orchestrates measurement sources such as the ambient light sensor.
 */
import javax.inject.Inject;

public class MeasurementEngine implements MeasurementRepository {
    private final LightSensorProvider lightSensorProvider;

    @Inject
    public MeasurementEngine(LightSensorProvider provider) {
        this.lightSensorProvider = provider;
    }

    /**
     * Observe lux readings from the sensor.
     */
    public Observable<Float> observeLux() {
        return lightSensorProvider.observeLux();
    }

    public void stopALS() {
        lightSensorProvider.stop();
    }
}
