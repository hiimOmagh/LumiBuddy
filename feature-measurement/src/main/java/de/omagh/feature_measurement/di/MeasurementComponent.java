package de.omagh.feature_measurement.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_measurement.ui.MeasureFragment;
import de.omagh.feature_measurement.ui.MeasureViewModel;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureViewModel viewModel);

    void inject(MeasureFragment fragment);

    @Component.Factory
    interface Factory {
        MeasurementComponent create(CoreComponent core);
    }
}