package de.omagh.core_infra.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Provides application wide executors.
 */
@Module
public class ExecutorModule {
    @Provides
    @Singleton
    AppExecutors provideAppExecutors() {
        return new AppExecutors();
    }
}