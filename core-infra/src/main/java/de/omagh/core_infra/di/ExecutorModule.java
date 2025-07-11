package de.omagh.core_infra.di;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Provides application wide executors.
 */
@Module
public class ExecutorModule {
    @Provides
    AppExecutors provideAppExecutors() {
        return new AppExecutors();
    }
}