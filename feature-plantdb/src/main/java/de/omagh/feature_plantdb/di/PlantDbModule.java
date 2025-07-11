package de.omagh.feature_plantdb.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.feature_plantdb.ui.PlantDbViewModelFactory;
import de.omagh.feature_plantdb.ui.PlantListViewModel;
import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;

import javax.inject.Provider;

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
    static PlantIdentifier providePlantIdentifier(Application app, AppExecutors executors) {
        return new PlantIdentifier(app.getApplicationContext(), executors);
    }

    @Provides
    static PlantDbViewModelFactory provideViewModelFactory(
            Provider<PlantListViewModel> listProvider,
            Provider<AddPlantViewModel> addProvider,
            Provider<PlantDetailViewModel> detailProvider) {
        return new PlantDbViewModelFactory(listProvider, addProvider, detailProvider);
    }
}