package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.measurement.ALSLightSensorProvider;
import de.omagh.core_infra.measurement.ALSManager;
import de.omagh.core_infra.measurement.CameraSpectralProvider;
import de.omagh.core_infra.measurement.LightSensorProvider;
import de.omagh.core_infra.measurement.MeasurementEngine;
import de.omagh.core_infra.measurement.StubCameraSpectralProvider;

/**
 * Provides sensor-related implementations.
 */
@Module
public abstract class SensorModule {
    @Provides
    @Singleton
    static ALSManager provideAlsManager(Application app) {
        return new ALSManager(app);

    }

    @Provides
    @Singleton
    static MeasurementEngine provideMeasurementEngine(LightSensorProvider provider) {
        return new MeasurementEngine(provider);
    }

    @Binds
    @Singleton
    abstract LightSensorProvider bindLightSensorProvider(ALSLightSensorProvider impl);

    @Binds
    @Singleton
    abstract CameraSpectralProvider bindCameraSpectralProvider(StubCameraSpectralProvider impl);

    @Binds
    @Singleton
    abstract MeasurementRepository bindMeasurementRepository(MeasurementEngine engine);
}