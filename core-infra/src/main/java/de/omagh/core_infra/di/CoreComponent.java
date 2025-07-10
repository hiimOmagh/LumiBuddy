package de.omagh.core_infra.di;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;

import javax.inject.Singleton;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.core_infra.user.UserProfileSyncManager;
import de.omagh.core_infra.user.LightCorrectionStore;
import de.omagh.core_infra.firebase.FirebaseManager;

@Singleton
@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class,
        UserModule.class
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

    LightCorrectionStore lightCorrectionStore();

    FirebaseManager firebaseManager();

    Application application();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
