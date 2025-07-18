package de.omagh.lumibuddy.di;

/**
 * Application level component that injects the main entry points.
 * It depends on {@link de.omagh.core_infra.di.CoreComponent} to obtain
 * core services.
 */

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.ui.MainActivity;

@Singleton
@Component(dependencies = CoreComponent.class)
public interface AppComponent {
    void inject(LumiBuddyApplication app);

    void inject(MainActivity activity);

    @Component.Factory
    interface Factory {
        AppComponent create(CoreComponent core);
    }
}