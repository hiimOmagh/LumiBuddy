package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import de.omagh.feature_plantdb.plantid.PlantIdRepository;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    private final PlantIdRepository plantIdRepository;

    public AddPlantViewModel(@NonNull Application application) {
        super(application);
        plantIdRepository = new PlantIdRepository();
    }

    public LiveData<PlantIdSuggestion> identifyPlant(Bitmap bitmap) {
        return plantIdRepository.identifyPlant(bitmap);
    }
}
