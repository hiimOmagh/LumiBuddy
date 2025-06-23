package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.model.PlantCareProfileEntity;
import de.omagh.core_domain.model.PlantSpecies;
import de.omagh.lumibuddy.feature_plantdb.PlantInfoRepository;
import de.omagh.lumibuddy.feature_plantdb.PlantRepository;
import de.omagh.lumibuddy.LumiBuddyApplication;

import javax.inject.Inject;

/**
 * ViewModel for PlantDetailFragment.
 * Holds LiveData for a single plant, supporting detail view and editing.
 * Can be extended to load/update a plant from Room database.
 */
public class PlantDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Plant> plant = new MutableLiveData<>();
    @Inject
    PlantRepository repository;
    private final PlantInfoRepository infoRepository;

    public PlantDetailViewModel(@NonNull Application application) {
        super(application);
        ((LumiBuddyApplication) application).getCoreComponent().inject(this);
        infoRepository = new PlantInfoRepository(application);
    }

    /**
     * Returns observable plant data for the detail screen.
     */
    public LiveData<Plant> getPlant() {
        return plant;
    }

    /**
     * Sets the current plant (from navigation args or DB).
     */
    public void setPlant(Plant plant) {
        this.plant.setValue(plant);
    }

    /**
     * Updates name and type of the current plant in memory.
     * Note: Does not persist to DB — call repo from host if needed.
     */
    public void updatePlantDetails(String name, String type) {
        Plant current = plant.getValue();
        if (current != null) {
            Plant updated = new Plant(current.getId(), name, type, current.getImageUri());
            plant.setValue(updated);
        }
    }

    /**
     * Persist the currently edited plant to the repository.
     */
    public void savePlant() {
        Plant current = plant.getValue();
        if (current != null) {
            repository.updatePlant(current);
        }
    }

    /**
     * Retrieves plant information for the given name.
     *
     * @param name plant name
     * @return matching PlantInfo or null
     */
    public LiveData<List<PlantSpecies>> searchSpecies(String name) {
        return infoRepository.searchSpecies(name);
    }

    public LiveData<List<PlantCareProfileEntity>> getCareProfile(String speciesId) {
        return infoRepository.getCareProfile(speciesId);
    }
}