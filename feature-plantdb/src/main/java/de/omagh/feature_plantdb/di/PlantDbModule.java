package de.omagh.feature_plantdb.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.feature_plantdb.data.PlantDatabaseManager;
import de.omagh.feature_plantdb.data.PlantInfoRepository;
import de.omagh.feature_plantdb.plantid.PlantIdRepository;

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