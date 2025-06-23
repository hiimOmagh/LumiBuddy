package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager;
import de.omagh.lumibuddy.feature_plantdb.PlantInfo;
import de.omagh.lumibuddy.feature_plantdb.PlantInfoRepository;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.lumibuddy.LumiBuddyApplication;

import javax.inject.Inject;

/**
 * ViewModel for managing the user's plant list.
 * Uses Room database (AppDatabase) for persistence.
 * Exposes LiveData for automatic UI updates.
 */
public class PlantListViewModel extends AndroidViewModel {
    private final LiveData<List<Plant>> plants;
    @Inject
    PlantRepository repository;
    private final PlantInfoRepository infoRepository;
    private final PlantDatabaseManager sampleDb;

    /**
     * Initializes the ViewModel and loads LiveData from Room DB.
     *
     * @param application Android Application context (required for DB singleton)
     */
    public PlantListViewModel(@NonNull Application application) {
        super(application);
        ((LumiBuddyApplication) application).getCoreComponent().inject(this);
        infoRepository = new PlantInfoRepository(application);
        sampleDb = new PlantDatabaseManager();
        plants = repository.getAllPlants();
    }

    /**
     * Gets all plants as LiveData.
     */
    public LiveData<List<Plant>> getPlants() {
        return plants;
    }

    /**
     * Gets a plant by its ID (for detail view).
     */
    public LiveData<Plant> getPlantById(long id) {
        return repository.getPlant(id);
    }

    /**
     * Adds a plant to the database asynchronously.
     */
    public void addPlant(Plant plant) {
        repository.insertPlant(plant);
    }

    /**
     * Updates a plant in the database asynchronously.
     */
    public void updatePlant(Plant plant) {
        repository.updatePlant(plant);
    }

    /**
     * Deletes a plant from the database asynchronously.
     */
    public void deletePlant(Plant plant) {
        repository.deletePlant(plant);
    }

    /**
     * Returns plant information for the given name using the sample database.
     *
     * @param name search query
     * @return matching PlantInfo or null
     */
    public LiveData<List<PlantSpecies>> searchPlantInfo(String name) {
        return infoRepository.searchSpecies(name);
    }

    public LiveData<List<PlantCareProfileEntity>> getCareProfile(String speciesId) {
        return infoRepository.getCareProfile(speciesId);
    }

    /**
     * Returns a list of all built-in plant info records.
     *
     * @return immutable list of {@link PlantInfo} objects
     */
    public List<PlantInfo> getAllPlantInfo() {
        return sampleDb.getAllPlants();
    }
// Repository manages its own executor; nothing to clean up here.
}