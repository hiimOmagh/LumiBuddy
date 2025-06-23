package de.omagh.core_infra.measurement;

import io.reactivex.Observable;

/**
 * Orchestrates measurement sources such as the ambient light sensor.
 */
import javax.inject.Inject;

public class MeasurementEngine {
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
