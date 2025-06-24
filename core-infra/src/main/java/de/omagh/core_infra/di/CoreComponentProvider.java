package de.omagh.core_infra.di;

/**
 * Interface to expose {@link CoreComponent} from an application context.
 */
public interface CoreComponentProvider {
    CoreComponent getCoreComponent();
}