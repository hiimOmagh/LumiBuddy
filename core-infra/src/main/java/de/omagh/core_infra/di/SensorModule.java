package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_infra.measurement.ALSLightSensorProvider;
import de.omagh.core_infra.measurement.LightSensorProvider;
import de.omagh.core_infra.measurement.CameraSpectralProvider;
import de.omagh.core_infra.measurement.StubCameraSpectralProvider;

/**
 * Provides sensor-related implementations.
 */
@Module
public class SensorModule {
    @Provides
    @Singleton
    LightSensorProvider provideLightSensor(Application app) {
        return new ALSLightSensorProvider(app);
    }

    @Provides
    @Singleton
    CameraSpectralProvider provideCameraSpectralProvider() {
        return new StubCameraSpectralProvider();
    }
}