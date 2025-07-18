package de.omagh.core_infra.di;

/**
 * Root component exposing singletons used across all features. Other Dagger
 * components depend on this component to access repositories, managers and
 * executors.
 */

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;

import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.core_infra.user.UserProfileSyncManager;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_infra.di.ExecutorModule;
import de.omagh.core_infra.di.SyncModule;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.sync.PlantSyncManager;
import de.omagh.core_infra.sync.DiarySyncManager;
import de.omagh.core_infra.di.Remote;
import de.omagh.core_infra.environment.SunlightEstimator;

@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class,
        UserModule.class,
        ExecutorModule.class,
        SyncModule.class
})

public interface CoreComponent {
    PlantRepository plantRepository();

    MeasurementRepository measurementRepository();

    /**
     * Exposes the Room database instance so feature components depending on
     * {@link CoreComponent} can obtain database DAOs.
     */
    AppDatabase appDatabase();

    SettingsManager settingsManager();

    CalibrationProfilesManager calibrationProfilesManager();

    UserProfileManager userProfileManager();

    UserProfileSyncManager userProfileSyncManager();

    LightCorrectionRepository lightCorrectionRepository();

    FirebaseManager firebaseManager();

    PlantSyncManager plantSyncManager();

    DiarySyncManager diarySyncManager();

    SunlightEstimator sunlightEstimator();

    DiaryRepository diaryRepository();

    AppExecutors appExecutors();

    @Remote
    PlantDataSource remotePlantRepository();

    @Remote
    DiaryDataSource remoteDiaryRepository();

    Application application();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
