package de.omagh.feature_plantdb.ui;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import javax.inject.Inject;

import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.feature_plantdb.di.PlantDbComponent;

/**
 * ViewModel for AddPlantFragment handling photo identification via Plant.id.
 */
public class AddPlantViewModel extends AndroidViewModel {
    @Inject
    PlantIdentifier plantIdentifier;

    @Inject
    public AddPlantViewModel(@NonNull Application application, PlantIdentifier plantIdentifier) {
        super(application);
        this.plantIdentifier = plantIdentifier;
    }

    public LiveData<String> identifyPlant(Bitmap bitmap) {
        return plantIdentifier.identifyPlant(bitmap);
    }
}
