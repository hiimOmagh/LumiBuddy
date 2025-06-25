package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.core_infra.user.UserProfileSyncManager;
import de.omagh.feature_measurement.di.MeasurementModule;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_plantdb.di.PlantDbModule;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.feature_plantdb.ui.PlantListViewModel;
import de.omagh.lumibuddy.LumiBuddyApplication;

@Singleton
@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class,
        UserModule.class,
        MeasurementModule.class,
        PlantDbModule.class
})
public interface CoreComponent {
    PlantRepository plantRepository();

    MeasurementRepository measurementRepository();

    SettingsManager settingsManager();

    CalibrationProfilesManager calibrationProfilesManager();

    UserProfileManager userProfileManager();

    UserProfileSyncManager userProfileSyncManager();

    void inject(LumiBuddyApplication application);

    void inject(MeasureViewModel viewModel);

    void inject(PlantListViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
