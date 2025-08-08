package de.omagh.core_infra.plantdb;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import javax.inject.Inject;

import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.shared_ml.IdentifierResult;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;

/**
 * Use case that performs on-device plant identification and falls back to the
 * Plant.id API when the local classifier is uncertain.
 */
public class PlantIdentificationUseCase {
    private final PlantIdentifier plantIdentifier;
    private final PlantIdRepository plantIdRepository;

    @Inject
    public PlantIdentificationUseCase(PlantIdentifier plantIdentifier,
                                      PlantIdRepository plantIdRepository) {
        this.plantIdentifier = plantIdentifier;
        this.plantIdRepository = plantIdRepository;
    }

    /**
     * Attempts to identify the plant in the given bitmap. First uses the
     * on-device model and falls back to the remote Plant.id API when the
     * top prediction is missing or below the confidence threshold.
     */
    public LiveData<PlantIdSuggestion> identify(Bitmap bitmap) {
        MediatorLiveData<PlantIdSuggestion> result = new MediatorLiveData<>();
        result.setValue(null);
        LiveData<IdentifierResult<List<Prediction>>> local = plantIdentifier.identifyPlant(bitmap);
        result.addSource(local, res -> {
            result.removeSource(local);
            if (res instanceof IdentifierResult.Success) {
                List<Prediction> predictions =
                        ((IdentifierResult.Success<List<Prediction>>) res).getValue();
                Prediction top = (predictions == null || predictions.isEmpty()) ? null : predictions.get(0);
                if (top == null || top.getConfidence() < plantIdentifier.getThreshold()) {
                    LiveData<PlantIdSuggestion> remote = plantIdRepository.identifyPlant(bitmap);
                    result.addSource(remote, suggestion -> {
                        result.postValue(suggestion);
                        result.removeSource(remote);
                    });
                } else {
                    result.postValue(new PlantIdSuggestion(top.getLabel(), top.getLabel()));
                }
            } else {
                LiveData<PlantIdSuggestion> remote = plantIdRepository.identifyPlant(bitmap);
                result.addSource(remote, suggestion -> {
                    result.postValue(suggestion);
                    result.removeSource(remote);
                });
            }
        });
        return result;
    }

    /**
     * Release ML resources when the caller is done.
     */
    public void close() {
        plantIdentifier.close();
    }
}
