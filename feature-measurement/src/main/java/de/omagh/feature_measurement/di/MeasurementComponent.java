package de.omagh.feature_measurement.di;

import javax.inject.Singleton;

import dagger.Subcomponent;
import de.omagh.core_infra.di.feature.MeasurementModule;
import de.omagh.feature_measurement.ui.MeasureViewModel;

@Singleton
@Subcomponent(modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureViewModel viewModel);

    @Subcomponent.Factory
    interface Factory {
        MeasurementComponent create();
    }
}