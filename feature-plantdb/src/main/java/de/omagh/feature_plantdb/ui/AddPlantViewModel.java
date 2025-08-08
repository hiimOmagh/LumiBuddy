package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.IdentifierResult;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdentificationUseCase;
import de.omagh.feature_plantdb.di.PlantDbComponent;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    private LiveData<PlantIdSuggestion> identificationResult = new MutableLiveData<>();
    @Inject
    PlantIdentifier plantIdentifier;
    @Inject
    PlantIdentificationUseCase plantIdentificationUseCase;

    @Inject
    public AddPlantViewModel(@NonNull Application application,
                             PlantIdentifier plantIdentifier,
                             PlantIdentificationUseCase plantIdentificationUseCase) {
        super(application);
        this.plantIdentifier = plantIdentifier;
        this.plantIdentificationUseCase = plantIdentificationUseCase;
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
        identificationResult = plantIdentificationUseCase.identify(bitmap);
        return identificationResult;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        plantIdentificationUseCase.close();
    }
}
