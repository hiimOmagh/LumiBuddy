package de.omagh.core_infra.di;

import de.omagh.lumibuddy.core.ApplicationComponent;

/**
 * Interface to expose {@link ApplicationComponent} from an application context.
 * Feature modules can cast their {@link android.app.Application} instance to this
 * interface to retrieve the component for injection.
 */
public interface AppComponentProvider {
    ApplicationComponent getAppComponent();
}