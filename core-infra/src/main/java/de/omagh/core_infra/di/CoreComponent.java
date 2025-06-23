package de.omagh.core_infra.di;

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.ui.MeasureViewModel;
import de.omagh.lumibuddy.ui.PlantDetailViewModel;
import de.omagh.lumibuddy.ui.PlantListViewModel;

@Singleton
@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class
})
public interface CoreComponent {
    void inject(LumiBuddyApplication app);

    void inject(MeasureViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    void inject(PlantListViewModel viewModel);
}