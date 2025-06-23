package de.omagh.core_infra.di;

import android.content.Context;
import android.hardware.SensorManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SensorModule {
    private final Context context;

    public SensorModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    SensorManager provideSensorManager() {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }
}
