package de.omagh.feature_plantdb.di;

/**
 * Module that provides ML and repository classes for the Plant database feature.
 * Used by {@link PlantDbComponent} and depends on core infrastructure for
 * executors and application context.
 */

import android.app.Application;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.core_infra.plantdb.PlantIdRepository;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.AssetModelProvider;
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
        AssetModelProvider provider = new AssetModelProvider("plant_identifier.tflite");
        return new PlantIdentifier(app.getApplicationContext(), provider, executors, "plant_labels.txt", 0.7f);
    }

    @Provides
    static PlantIdRepository providePlantIdRepository(AppExecutors executors) {
        return new PlantIdRepository(executors);
    }

    @Provides
    static PlantDbViewModelFactory provideViewModelFactory(
            Provider<PlantListViewModel> listProvider,
            Provider<AddPlantViewModel> addProvider,
            Provider<PlantDetailViewModel> detailProvider) {
        return new PlantDbViewModelFactory(listProvider, addProvider, detailProvider);
    }
}