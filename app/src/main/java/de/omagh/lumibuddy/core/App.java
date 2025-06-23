package de.omagh.lumibuddy.core;

import android.app.Application;

import timber.log.Timber;
import de.omagh.lumibuddy.BuildConfig;

/**
 * Custom Application class that sets up Dagger and logging.
 */
public class App extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .sensorModule(new de.omagh.lumibuddy.core_infra.di.SensorModule(this))
                .build();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
