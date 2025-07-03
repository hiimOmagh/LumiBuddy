package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.feature_diary.di.DiaryComponent;
import de.omagh.feature_measurement.di.MeasurementComponent;
import de.omagh.feature_plantdb.di.PlantDbComponent;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.core_infra.user.UserProfileSyncManager;

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

    SettingsManager settingsManager();

    CalibrationProfilesManager calibrationProfilesManager();

    UserProfileManager userProfileManager();

    UserProfileSyncManager userProfileSyncManager();

    Application application();

    DiaryComponent.Factory diaryComponent();

    MeasurementComponent.Factory measurementComponent();

    PlantDbComponent.Factory plantDbComponent();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
