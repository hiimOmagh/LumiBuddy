package de.omagh.feature_measurement.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_measurement.ui.MeasureFragment;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;

@Component(dependencies = CoreComponent.class, modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureFragment fragment);

    MeasureViewModelFactory viewModelFactory();

    @Component.Factory
    interface Factory {
        MeasurementComponent create(CoreComponent core);
    }
}