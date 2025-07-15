package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import javax.inject.Inject;

import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdRepository;
import de.omagh.feature_plantdb.di.PlantDbComponent;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    @Inject
    PlantIdentifier plantIdentifier;

    @Inject
    PlantIdRepository plantIdRepository;

    @Inject
    public AddPlantViewModel(@NonNull Application application,
                             PlantIdentifier plantIdentifier,
                             PlantIdRepository plantIdRepository) {
        super(application);
        this.plantIdentifier = plantIdentifier;
        this.plantIdRepository = plantIdRepository;
    }

    public LiveData<String> identifyPlant(Bitmap bitmap) {
        return plantIdentifier.identifyPlant(bitmap);
    }

    /**
     * Performs on-device identification and falls back to Plant.id API when
     * the local classifier returns "Unknown".
     */
    public LiveData<PlantIdSuggestion> identifyPlantWithApi(Bitmap bitmap) {
        MediatorLiveData<PlantIdSuggestion> result = new MediatorLiveData<>();
        LiveData<String> local = plantIdentifier.identifyPlant(bitmap);
        result.addSource(local, label -> {
            result.removeSource(local);
            if ("Unknown".equals(label)) {
                LiveData<PlantIdSuggestion> remote = plantIdRepository.identifyPlant(bitmap);
                result.addSource(remote, suggestion -> {
                    result.postValue(suggestion);
                    result.removeSource(remote);
                });
            } else {
                result.postValue(new PlantIdSuggestion(label, label));
            }
        });
        return result;
    }
}
