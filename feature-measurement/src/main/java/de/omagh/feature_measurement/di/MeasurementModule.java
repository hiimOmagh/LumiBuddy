package de.omagh.feature_measurement.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_measurement.ui.LampProfilesViewModel;
import de.omagh.feature_measurement.ui.LampProfilesViewModelFactory;
import de.omagh.shared_ml.AssetModelProvider;
import de.omagh.shared_ml.LampIdentifier;

import de.omagh.core_infra.calibration.CalibrationRepository;
import de.omagh.feature_measurement.infra.GrowLightProductRepository;

import javax.inject.Provider;

/**
 * Dagger module for the Measurement feature. Provides use cases and helpers
 * needed by {@link de.omagh.feature_measurement.ui.MeasureFragment} and
 * related view models.
 */
@Module
public class MeasurementModule {
    @Provides
    static GetCurrentLuxUseCase provideGetCurrentLuxUseCase(MeasurementRepository repo) {
        return new GetCurrentLuxUseCase(repo);
    }

    @Provides
    static CalibrationManager provideCalibrationManager(Application app) {
        return new CalibrationManager(app.getApplicationContext());
    }

    @Provides
    static GrowLightProfileManager provideGrowLightProfileManager(Application app) {
        return new GrowLightProfileManager(app.getApplicationContext());
    }

    @Provides
    static SettingsManager provideSettingsManager(Application app) {
        return new SettingsManager(app.getApplicationContext());
    }

    @Provides
    static GrowLightProductRepository provideGrowLightProductRepository(Application app) {
        return new GrowLightProductRepository(app.getApplicationContext());
    }

    @Provides
    static LampIdentifier provideLampIdentifier(Application app, CalibrationRepository repo) {
        AssetModelProvider provider = new AssetModelProvider("lamp_identifier.tflite");
        return new LampIdentifier(app.getApplicationContext(), provider, "lamp_labels.txt", repo.getMlThreshold());
    }

    @Provides
    static MeasureViewModelFactory provideViewModelFactory(Provider<MeasureViewModel> provider) {
        return new MeasureViewModelFactory(provider);
    }

    @Provides
    static LampProfilesViewModelFactory provideLampProfilesViewModelFactory(Provider<LampProfilesViewModel> provider) {
        return new LampProfilesViewModelFactory(provider);
    }
}
