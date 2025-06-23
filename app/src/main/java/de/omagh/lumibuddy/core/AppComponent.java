package de.omagh.lumibuddy.core;

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.lumibuddy.core_infra.di.SensorModule;
import de.omagh.lumibuddy.ui.MeasureViewModel;

/**
 * Application-level Dagger component.
 */
@Singleton
@Component(modules = {SensorModule.class})
public interface AppComponent {
    void inject(MeasureViewModel viewModel);
}