package de.omagh.lumibuddy.core;

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.lumibuddy.ui.MeasureViewModel;
import de.omagh.lumibuddy.ui.PlantDetailViewModel;
import de.omagh.lumibuddy.ui.PlantListViewModel;

/**
 * Application-level Dagger component.
 */
@Singleton
@Component(dependencies = CoreComponent.class)
public interface AppComponent {
    void inject(MeasureViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    void inject(PlantListViewModel viewModel);
}