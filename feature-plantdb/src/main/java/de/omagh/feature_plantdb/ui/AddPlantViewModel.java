package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.plantdb.PlantIdRepository;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.feature_plantdb.di.PlantDbComponent;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    @Inject
    PlantIdRepository plantIdRepository;

    @Inject
    public AddPlantViewModel(@NonNull Application application) {
        super(application);
        PlantDbComponent component = ((CoreComponentProvider) application)
                .getCoreComponent()
                .plantDbComponent()
                .create();
        component.inject(this);
    }

    public LiveData<PlantIdSuggestion> identifyPlant(Bitmap bitmap) {
        return plantIdRepository.identifyPlant(bitmap);
    }
}
