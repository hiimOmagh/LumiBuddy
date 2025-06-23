package de.omagh.lumibuddy.core;

import android.app.Application;

import timber.log.Timber;
import de.omagh.lumibuddy.BuildConfig;

import com.squareup.leakcanary.LeakCanary;

/**
 * Custom Application class that sets up Dagger and logging.
 */
public class App extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .sensorModule(new de.omagh.core_infra.di.SensorModule())
                .build();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
            LeakCanary.INSTANCE.install(this);
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
