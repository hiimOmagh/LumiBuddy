package de.omagh.lumibuddy.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.ui.MainActivity;

@Component(dependencies = CoreComponent.class)
public interface AppComponent {
    void inject(LumiBuddyApplication app);

    void inject(MainActivity activity);

    @Component.Factory
    interface Factory {
        AppComponent create(CoreComponent core);
    }
}