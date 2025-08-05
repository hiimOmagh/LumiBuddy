package de.omagh.feature_measurement.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_measurement.ui.MeasureFragment;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;
import de.omagh.feature_measurement.ui.LampProfilesFragment;
import de.omagh.feature_measurement.ui.LampProfilesViewModelFactory;

/**
 * Dagger component for the Measurement feature. It depends on
 * {@link de.omagh.core_infra.di.CoreComponent} and uses
 * {@link MeasurementModule} to provide feature specific bindings.
 */
@FeatureScope
@Component(dependencies = CoreComponent.class, modules = MeasurementModule.class)
public interface MeasurementComponent {
    void inject(MeasureFragment fragment);
    void inject(LampProfilesFragment fragment);

    MeasureViewModelFactory viewModelFactory();
    LampProfilesViewModelFactory lampProfilesViewModelFactory();

    @Component.Factory
    interface Factory {
        MeasurementComponent create(CoreComponent core);
    }
}