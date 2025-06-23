package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_data.repo.PlantDataSource;
import de.omagh.core_data.repo.PlantRepository;
import de.omagh.lumibuddy.data.db.AppDatabase;

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
    static PlantDao providePlantDao(AppDatabase db) {
        return db.plantDao();
    }

    @Binds
    @Singleton
    abstract PlantDataSource bindPlantRepository(PlantRepository impl);
}
