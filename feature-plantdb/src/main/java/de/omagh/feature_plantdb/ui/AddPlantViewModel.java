package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.feature_plantdb.plantid.PlantIdRepository;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    @Inject
    PlantIdRepository plantIdRepository;

    @Inject
    public AddPlantViewModel(@NonNull Application application) {
        super(application);
        ((CoreComponentProvider) application).getCoreComponent().inject(this);
    }

    public LiveData<PlantIdSuggestion> identifyPlant(Bitmap bitmap) {
        return plantIdRepository.identifyPlant(bitmap);
    }
}
