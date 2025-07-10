package de.omagh.core_infra.di;

import android.app.Application;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.PlantRepository;

/**
 * Provides data layer dependencies.
 */
@Module
public abstract class DataModule {
    @Provides
    @Singleton
    static AppDatabase provideDatabase(Application app) {
        return AppDatabase.getInstance(app);
    }

    @Provides
    @Singleton
    static PlantDao providePlantDao(AppDatabase db) {
        return db.plantDao();
    }

    @Provides
    @Singleton
    static PlantRepository providePlantRepository(AppDatabase db) {
        return new PlantRepository(db);
    }

    @Binds

    abstract PlantDataSource bindPlantRepository(PlantRepository impl);
}
