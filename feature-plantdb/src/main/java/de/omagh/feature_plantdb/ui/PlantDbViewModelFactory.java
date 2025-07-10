package de.omagh.feature_plantdb.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Factory for PlantDB feature ViewModels backed by Dagger providers.
 */
public class PlantDbViewModelFactory implements ViewModelProvider.Factory {
    private final Provider<PlantListViewModel> listProvider;
    private final Provider<AddPlantViewModel> addProvider;
    private final Provider<PlantDetailViewModel> detailProvider;

    @Inject
    public PlantDbViewModelFactory(Provider<PlantListViewModel> listProvider,
                                   Provider<AddPlantViewModel> addProvider,
                                   Provider<PlantDetailViewModel> detailProvider) {
        this.listProvider = listProvider;
        this.addProvider = addProvider;
        this.detailProvider = detailProvider;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PlantListViewModel.class)) {
            return (T) listProvider.get();
        } else if (modelClass.isAssignableFrom(AddPlantViewModel.class)) {
            return (T) addProvider.get();
        } else if (modelClass.isAssignableFrom(PlantDetailViewModel.class)) {
            return (T) detailProvider.get();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}