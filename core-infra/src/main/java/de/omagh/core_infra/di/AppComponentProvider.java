package de.omagh.core_infra.di;

/**
 * Interface to expose {@link de.omagh.lumibuddy.core.AppComponent} from an application context.
 * Feature modules can cast their {@link android.app.Application} instance to this
 * interface to retrieve the component for injection.
 */
public interface AppComponentProvider {
    Object getAppComponent();
}