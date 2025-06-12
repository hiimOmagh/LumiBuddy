package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.feature_plantdb.PlantRepository;
import de.omagh.lumibuddy.feature_plantdb.PlantInfoRepository;
import de.omagh.lumibuddy.feature_plantdb.PlantInfo;
import de.omagh.lumibuddy.feature_plantdb.PlantStage;
import de.omagh.lumibuddy.feature_plantdb.PlantCareProfile;

import org.jspecify.annotations.NonNull;

/**
 * ViewModel for PlantDetailFragment.
 * Holds LiveData for a single plant, supporting detail view and editing.
 * Can be extended to load/update a plant from Room database.
 */
public class PlantDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<Plant> plant = new MutableLiveData<>();
    private final PlantRepository repository;
    private final PlantInfoRepository infoRepository;

    public PlantDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new PlantRepository(AppDatabase.getInstance(application));
        infoRepository = new PlantInfoRepository();
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
    public PlantInfo getPlantInfo(String name) {
        return infoRepository.identifyPlant(name);
    }

    /**
     * Returns a care profile for the specified stage if available.
     *
     * @param name  plant name
     * @param stage desired growth stage
     * @return care profile or null
     */
    public PlantCareProfile getCareProfile(String name, PlantStage stage) {
        PlantInfo info = infoRepository.identifyPlant(name);
        return info != null ? info.getProfileForStage(stage) : null;
    }

    /**
     * Returns all available sample plant information.
     *
     * @return immutable list of PlantInfo entries
     */
    public java.util.List<PlantInfo> getAllPlantInfo() {
        return infoRepository.getAllPlantInfo();
    }
}