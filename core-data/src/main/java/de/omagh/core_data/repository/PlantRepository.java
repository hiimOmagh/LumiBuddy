package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_domain.model.Plant;

public class PlantRepository implements PlantDataSource {
    private final PlantDao plantDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PlantRepository(AppDatabase db) {
        this.plantDao = db.plantDao();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantDao.getAll();
    }

    public LiveData<Plant> getPlant(String id) {
        return plantDao.getById(id);
    }

    public void insertPlant(Plant plant) {
        executor.execute(() -> plantDao.insert(plant));
    }

    public void updatePlant(Plant plant) {
        executor.execute(() -> plantDao.update(plant));
    }

    public void deletePlant(Plant plant) {
        executor.execute(() -> plantDao.delete(plant));
    }
}

