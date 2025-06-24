package de.omagh.lumibuddy.core;

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.ApplicationComponent;

/**
 * Application-level Dagger component.
 */
@Singleton
@Component(dependencies = CoreComponent.class)
public interface AppComponent extends ApplicationComponent {
}