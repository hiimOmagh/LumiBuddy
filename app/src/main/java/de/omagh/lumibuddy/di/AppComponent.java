package de.omagh.lumibuddy.di;

import javax.inject.Singleton;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.ui.MainActivity;

@Singleton
@Component(dependencies = CoreComponent.class)
public interface AppComponent {
    void inject(LumiBuddyApplication application);
    void inject(MainActivity activity);

    @Component.Factory
    interface Factory {
        AppComponent create(CoreComponent core);
    }
}