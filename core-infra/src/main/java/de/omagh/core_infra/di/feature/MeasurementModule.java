package de.omagh.core_infra.di.feature;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;

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
}