package de.omagh.core_infra.measurement;

import javax.inject.Inject;

import de.omagh.core_domain.repository.MeasurementRepository;
import io.reactivex.rxjava3.core.Observable;

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
