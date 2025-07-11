package de.omagh.core_infra.di;

import android.app.Application;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Provides data layer dependencies.
 */
@Module
public abstract class DataModule {
    @Provides
    static AppDatabase provideDatabase(Application app) {
        return AppDatabase.getInstance(app);
    }

    @Provides
    static PlantDao providePlantDao(AppDatabase db) {
        return db.plantDao();
    }

    @Provides
    static PlantRepository providePlantRepository(AppDatabase db, AppExecutors executors) {
        return new PlantRepository(db, executors);
    }

    @Binds

    abstract PlantDataSource bindPlantRepository(PlantRepository impl);
}
