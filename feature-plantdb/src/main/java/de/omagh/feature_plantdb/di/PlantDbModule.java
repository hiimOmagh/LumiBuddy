package de.omagh.feature_plantdb.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.core_infra.plantdb.PlantIdRepository;

@Module
public class PlantDbModule {
    @Provides
    static PlantInfoRepository providePlantInfoRepository(Application app) {
        return new PlantInfoRepository(app.getApplicationContext());
    }

    @Provides
    static PlantDatabaseManager providePlantDatabaseManager() {
        return new PlantDatabaseManager();
    }

    @Provides
    static PlantIdRepository providePlantIdRepository() {
        return new PlantIdRepository();
    }
}