package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import org.jspecify.annotations.NonNull;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_plantdb.PlantRepository;

/**
 * ViewModel for managing the user's plant list.
 * Uses Room database (AppDatabase) for persistence.
 * Exposes LiveData for automatic UI updates.
 */
public class PlantListViewModel extends AndroidViewModel {
    private final LiveData<List<Plant>> plants;
    private final PlantRepository repository;

    /**
     * Initializes the ViewModel and loads LiveData from Room DB.
     *
     * @param application Android Application context (required for DB singleton)
     */
    public PlantListViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        repository = new PlantRepository(db);
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
// Repository manages its own executor; nothing to clean up here.
}