package de.omagh.lumibuddy;

import android.app.Application;

import timber.log.Timber;
import de.omagh.core_infra.di.DaggerCoreComponent;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;

/**
 * Simplified Application setup used for instrumentation tests.
 */
public class LumiBuddyApplication extends Application implements CoreComponentProvider {
    private CoreComponent coreComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        coreComponent = DaggerCoreComponent.builder()
                .application(this)
                .build();
        coreComponent.inject(this);
                .coreComponent(coreComponent)
                .build();
        Timber.plant(new Timber.DebugTree());
    }

    @Override
    public CoreComponent getCoreComponent() {
        return coreComponent;
    }
}
