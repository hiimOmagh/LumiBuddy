package de.omagh.feature_plantdb.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = PlantDbModule.class)
public interface PlantDbComponent {
    void inject(AddPlantViewModel viewModel);

    void inject(PlantListViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    @Component.Factory
    interface Factory {
        PlantDbComponent create(CoreComponent core);
    }
}