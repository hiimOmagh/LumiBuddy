package de.omagh.lumibuddy;

import android.app.Application;

import timber.log.Timber;
import de.omagh.core_infra.di.DaggerCoreComponent;
import de.omagh.core_infra.di.CoreComponent;

/**
 * Simplified Application setup used for instrumentation tests.
 */
public class LumiBuddyApplication extends Application {
    private CoreComponent coreComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        coreComponent = DaggerCoreComponent.create();
        coreComponent.inject(this);
        Timber.plant(new Timber.DebugTree());
    }

    public CoreComponent getCoreComponent() {
        return coreComponent;
    }
}