package de.omagh.lumibuddy;

import android.app.Application;

import timber.log.Timber;
import de.omagh.core_infra.di.DaggerCoreComponent;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.lumibuddy.core.AppComponent;
import de.omagh.lumibuddy.core.DaggerAppComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.di.AppComponentProvider;

/**
 * Simplified Application setup used for instrumentation tests.
 */
public class LumiBuddyApplication extends Application implements CoreComponentProvider, AppComponentProvider {
    private CoreComponent coreComponent;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        coreComponent = DaggerCoreComponent.builder()
                .application(this)
                .build();
        appComponent = DaggerAppComponent.builder()
                .coreComponent(coreComponent)
                .build();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    public CoreComponent getCoreComponent() {
        return coreComponent;
    }

    @Override
    public AppComponent getAppComponent() {
        return appComponent;
    }
}