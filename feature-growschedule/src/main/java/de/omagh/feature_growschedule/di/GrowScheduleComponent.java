package de.omagh.feature_growschedule.di;

/**
 * Component wiring the Grow Schedule feature. Depends on
 * {@link de.omagh.core_infra.di.CoreComponent} and incorporates
 * {@link GrowScheduleModule} to provide schedulers and repositories.
 */

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_growschedule.ui.HomeFragment;
import de.omagh.feature_growschedule.ui.HomeViewModelFactory;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = GrowScheduleModule.class)
public interface GrowScheduleComponent {
    HomeViewModelFactory viewModelFactory();

    void inject(HomeFragment fragment);

    @Component.Factory
    interface Factory {
        GrowScheduleComponent create(CoreComponent core);
    }
}