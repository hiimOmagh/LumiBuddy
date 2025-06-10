package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.model.Plant;

/**
 * ViewModel for managing the user's plant list.
 * Uses Room database (AppDatabase) for persistence.
 * Exposes LiveData for automatic UI updates.
 */
public class PlantListViewModel extends AndroidViewModel {
    private final LiveData<List<Plant>> plants;
    private final AppDatabase db;
    private final ExecutorService executor;

    /**
     * Initializes the ViewModel and loads LiveData from Room DB.
     *
     * @param application Android Application context (required for DB singleton)
     */
    public PlantListViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        plants = db.plantDao().getAll();
        executor = Executors.newSingleThreadExecutor();
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
        return db.plantDao().getById(id);
    }

    /**
     * Adds a plant to the database asynchronously.
     */
    public void addPlant(Plant plant) {
        executor.execute(() -> db.plantDao().insert(plant));
    }

    /**
     * Updates a plant in the database asynchronously.
     */
    public void updatePlant(Plant plant) {
        executor.execute(() -> db.plantDao().update(plant));
    }

    /**
     * Deletes a plant from the database asynchronously.
     */
    public void deletePlant(Plant plant) {
        executor.execute(() -> db.plantDao().delete(plant));
    }

    /**
     * Shut down the executor when the ViewModel is destroyed.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
