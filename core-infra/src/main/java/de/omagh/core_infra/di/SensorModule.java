package de.omagh.core_infra.di;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.location.LocationManager;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.measurement.ALSLightSensorProvider;
import de.omagh.core_infra.measurement.ALSManager;
import de.omagh.core_infra.measurement.CameraSpectralProvider;
import de.omagh.core_infra.measurement.LightSensorProvider;
import de.omagh.core_infra.measurement.MeasurementEngine;
import de.omagh.core_infra.measurement.StubCameraSpectralProvider;
import de.omagh.core_infra.environment.SunlightEstimator;

/**
 * Provides sensor-related implementations.
 */
@Module
public abstract class SensorModule {
    @Provides
    static ALSManager provideAlsManager(Application app) {
        return new ALSManager(app);

    }

    @Provides
    static MeasurementEngine provideMeasurementEngine(LightSensorProvider provider) {
        return new MeasurementEngine(provider);
    }

    @Provides
    static SunlightEstimator provideSunlightEstimator(Application app) {
        LocationManager lm = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        SensorManager sm = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);
        return new SunlightEstimator(lm, sm);
    }

    @Binds

    abstract LightSensorProvider bindLightSensorProvider(ALSLightSensorProvider impl);

    @Binds

    abstract CameraSpectralProvider bindCameraSpectralProvider(StubCameraSpectralProvider impl);

    @Binds

    abstract MeasurementRepository bindMeasurementRepository(MeasurementEngine engine);
}