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
        // Initialize Firebase so FirebaseAuth and Firestore can be used
        // even when the google-services Gradle plugin is not applied. We
        // guard the call to avoid creating multiple FirebaseApp instances
        // in instrumentation tests.
        if (com.google.firebase.FirebaseApp.getApps(this).isEmpty()) {
            com.google.firebase.FirebaseApp.initializeApp(this);
        }
        coreComponent = DaggerCoreComponent.builder()
                .application(this)
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        appComponent = DaggerAppComponent.factory()
                .create(coreComponent);
        appComponent.inject(this);

        // Schedule periodic sync on startup
        new de.omagh.core_infra.sync.SyncScheduler(this).scheduleDaily();
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
                        () -> coreComponent.firebaseManager()))
                .build();
    }
}
