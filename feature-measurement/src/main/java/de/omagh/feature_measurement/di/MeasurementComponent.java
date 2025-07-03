package de.omagh.feature_measurement.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.feature.MeasurementModule;
import de.omagh.feature_measurement.ui.MeasureViewModel;

@Component(dependencies = CoreComponent.class, modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureViewModel viewModel);

    @Component.Builder
    interface Builder {
        Builder coreComponent(CoreComponent coreComponent);

        MeasurementComponent build();
    }
}