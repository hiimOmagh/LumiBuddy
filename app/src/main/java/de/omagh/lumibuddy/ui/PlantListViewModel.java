package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

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

    /**
     * Initializes the ViewModel and loads LiveData from Room DB.
     *
     * @param application Android Application context (required for DB singleton)
     */
    public PlantListViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        plants = db.plantDao().getAll();
    }

    /**
     * Gets all plants as LiveData.
     */
    public LiveData<List<Plant>> getPlants() {
        return plants;
    }

    /**
     * Adds a plant to the database (on a background thread).
     */
    public void addPlant(Plant plant) {
        new Thread(() -> db.plantDao().insert(plant)).start();
    }

    /**
     * Deletes a plant from the database (on a background thread).
     */
    public void deletePlant(Plant plant) {
        new Thread(() -> db.plantDao().delete(plant)).start();
    }

    /**
     * Updates a plant in the database (on a background thread).
     */
    public void updatePlant(Plant plant) {
        new Thread(() -> db.plantDao().update(plant)).start();
    }
}
