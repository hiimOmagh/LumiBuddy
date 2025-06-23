package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.lumibuddy.feature_plantdb.PlantRepository;

@Module
public class DataModule {
    @Provides
    @Singleton
    AppDatabase provideDatabase(Application app) {
        return AppDatabase.getInstance(app);
    }

    @Provides
    PlantDao providePlantDao(AppDatabase db) {
        return db.plantDao();
    }

    @Provides
    @Singleton
    PlantRepository providePlantRepository(AppDatabase db) {
        return new PlantRepository(db);
    }
}