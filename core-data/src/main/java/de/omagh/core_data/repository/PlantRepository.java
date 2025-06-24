package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

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
        return Transformations.map(plantDao.getAll(), list -> {
            if (list == null) return null;
            java.util.List<Plant> result = new java.util.ArrayList<>();
            for (de.omagh.core_data.model.Plant p : list) {
                result.add(toDomain(p));
            }
            return result;
        });
    }

    public LiveData<Plant> getPlant(String id) {
        return Transformations.map(plantDao.getById(id), this::toDomain);
    }

    public void insertPlant(Plant plant) {
        executor.execute(() -> plantDao.insert(toEntity(plant)));
    }

    public void updatePlant(Plant plant) {
        executor.execute(() -> plantDao.update(toEntity(plant)));
    }

    public void deletePlant(Plant plant) {
        executor.execute(() -> plantDao.delete(toEntity(plant)));
    }

    private Plant toDomain(de.omagh.core_data.model.Plant entity) {
        if (entity == null) return null;
        return new Plant(entity.getId(), entity.getName(), entity.getType(), entity.getImageUri());
    }

    private de.omagh.core_data.model.Plant toEntity(Plant plant) {
        return new de.omagh.core_data.model.Plant(
                plant.getId(),
                plant.getName(),
                plant.getType(),
                plant.getImageUri()
        );
    }
}

