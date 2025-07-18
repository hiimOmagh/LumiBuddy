package de.omagh.core_data.repository.firebase;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_domain.model.Plant;

/**
 * Remote {@link PlantDataSource} implementation backed by Firebase Firestore.
 */
public class FirebasePlantRepository implements PlantDataSource {
    private final FirestorePlantDao dao;

    public FirebasePlantRepository(String uid) {
        this.dao = new FirestorePlantDao(uid);
    }
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<Plant>> getAllPlants() {
        return dao.getAll();
    }

    public List<Plant> getAllPlantsSync() {
        return dao.getAllSync();
    }

    public LiveData<Plant> getPlant(String id) {
        return dao.getById(id);
    }

    public void insertPlant(Plant plant) {
        executor.execute(() -> dao.insert(plant));
    }

    public void updatePlant(Plant plant) {
        executor.execute(() -> dao.update(plant));
    }

    public void deletePlant(Plant plant) {
        executor.execute(() -> dao.delete(plant));
    }
}
