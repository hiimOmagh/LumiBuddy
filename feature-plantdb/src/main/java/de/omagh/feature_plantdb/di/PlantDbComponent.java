package de.omagh.feature_plantdb.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_plantdb.ui.PlantDbViewModelFactory;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = PlantDbModule.class)
public interface PlantDbComponent {
    PlantDbViewModelFactory viewModelFactory();

    @Component.Factory
    interface Factory {
        PlantDbComponent create(CoreComponent core);
    }
}