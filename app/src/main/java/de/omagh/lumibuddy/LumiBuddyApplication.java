package de.omagh.lumibuddy;

import android.app.Application;

import timber.log.Timber;

import androidx.annotation.NonNull;
import androidx.work.Configuration;
import de.omagh.core_infra.di.DaggerCoreComponent;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.sync.SyncWorkerFactory;
import de.omagh.lumibuddy.di.AppComponent;
import de.omagh.lumibuddy.di.DaggerAppComponent;

/**
 * Simplified Application setup used for instrumentation tests.
 */
public class LumiBuddyApplication extends Application implements CoreComponentProvider, Configuration.Provider {
    private CoreComponent coreComponent;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        coreComponent = DaggerCoreComponent.builder()
                .application(this)
                .build();

        Timber.plant(new Timber.DebugTree());
        appComponent = DaggerAppComponent.factory()
                .create(coreComponent);
        appComponent.inject(this);
    }

    @Override
    public CoreComponent getCoreComponent() {
        return coreComponent;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(new SyncWorkerFactory(
                        () -> coreComponent.remotePlantRepository(),
                        () -> coreComponent.remoteDiaryRepository(),
                        () -> coreComponent.firebaseManager()))
                .build();
    }
}
