package de.omagh.feature_measurement.di;

/**
 * Dagger module for the Measurement feature. Provides use cases and helpers
 * needed by {@link de.omagh.feature_measurement.ui.MeasureFragment} and
 * related view models.
 */

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.shared_ml.AssetModelProvider;
import de.omagh.shared_ml.LampIdentifier;

import javax.inject.Provider;

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
    static LampIdentifier provideLampIdentifier(Application app) {
        AssetModelProvider provider = new AssetModelProvider("lamp_identifier.tflite");
        return new LampIdentifier(app.getApplicationContext(), provider, "lamp_labels.txt", 0.7f);
    }

    @Provides
    static MeasureViewModelFactory provideViewModelFactory(Provider<MeasureViewModel> provider) {
        return new MeasureViewModelFactory(provider);
    }
}