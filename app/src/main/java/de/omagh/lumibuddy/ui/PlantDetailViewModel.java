package de.omagh.lumibuddy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import de.omagh.lumibuddy.data.model.Plant;

/**
 * ViewModel for PlantDetailFragment.
 * Holds LiveData for a single plant, supporting detail view and editing.
 * Future: can be expanded to load/save from Room DB by ID.
 */
public class PlantDetailViewModel extends ViewModel {
    private final MutableLiveData<Plant> plant = new MutableLiveData<>();

    /**
     * Returns observable plant data for the detail screen.
     */
    public LiveData<Plant> getPlant() {
        return plant;
    }

    /**
     * Sets the current plant (from navigation arguments or loaded from database).
     */
    public void setPlant(Plant plant) {
        this.plant.setValue(plant);
    }

    // Future: add loadById(), updatePlant(), etc. as needed.
}
