package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdRepository;
import de.omagh.feature_plantdb.di.PlantDbComponent;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    private final MediatorLiveData<PlantIdSuggestion> identificationResult = new MediatorLiveData<>();
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

    public LiveData<PlantIdSuggestion> getIdentificationResult() {
        return identificationResult;
    }

    public LiveData<java.util.List<Prediction>> identifyPlant(Bitmap bitmap) {
        return plantIdentifier.identifyPlant(bitmap);
    }

    /**
     * Performs on-device identification and falls back to Plant.id API when
     * the local classifier reports low confidence. The ML component only
     * exposes a label when the confidence exceeds its threshold.
     */
    public LiveData<PlantIdSuggestion> identifyPlantWithApi(Bitmap bitmap) {
        identificationResult.setValue(null);
        LiveData<java.util.List<Prediction>> local = plantIdentifier.identifyPlant(bitmap);
        identificationResult.addSource(local, prediction -> {
            identificationResult.removeSource(local);
            Prediction top = (prediction == null || prediction.isEmpty()) ? null : prediction.get(0);
            if (top == null || top.getConfidence() < plantIdentifier.getThreshold()) {
                LiveData<PlantIdSuggestion> remote = plantIdRepository.identifyPlant(bitmap);
                identificationResult.addSource(remote, suggestion -> {
                    identificationResult.postValue(suggestion);
                    identificationResult.removeSource(remote);
                });
            } else {
                identificationResult.postValue(new PlantIdSuggestion(top.getLabel(), top.getLabel()));

            }
        });
        return identificationResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        plantIdentifier.close();
    }
}
