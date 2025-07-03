package de.omagh.feature_plantdb.di;

import javax.inject.Singleton;

import dagger.Subcomponent;
import de.omagh.core_infra.di.feature.PlantDbModule;
import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

@Singleton
@Subcomponent(modules = PlantDbModule.class)
public interface PlantDbComponent {
    void inject(AddPlantViewModel viewModel);

    void inject(PlantListViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    @Subcomponent.Factory
    interface Factory {
        PlantDbComponent create();
    }
}