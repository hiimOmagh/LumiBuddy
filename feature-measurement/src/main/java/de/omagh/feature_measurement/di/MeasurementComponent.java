package de.omagh.feature_measurement.di;

/**
 * Dagger component for the Measurement feature. It depends on
 * {@link de.omagh.core_infra.di.CoreComponent} and uses
 * {@link MeasurementModule} to provide feature specific bindings.
 */

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_measurement.ui.MeasureFragment;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureFragment fragment);

    MeasureViewModelFactory viewModelFactory();

    @Component.Factory
    interface Factory {
        MeasurementComponent create(CoreComponent core);
    }
}