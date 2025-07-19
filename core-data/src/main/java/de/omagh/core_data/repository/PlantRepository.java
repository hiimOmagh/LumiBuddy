package de.omagh.core_data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import java.util.List;
import java.util.concurrent.ExecutorService;

import de.omagh.core_domain.util.AppExecutors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_domain.model.Plant;

/**
 * Repository providing CRUD access to {@link de.omagh.core_domain.model.Plant} entities.
 * All operations run on a background executor and schedule a sync via
 * {@link de.omagh.core_infra.sync.SyncScheduler}.
 */
public class PlantRepository implements PlantDataSource {
    private final PlantDao plantDao;
    private final ExecutorService executor;
    private final de.omagh.core_infra.sync.SyncScheduler scheduler;
    private final Context context;

public PlantRepository(Context context, AppDatabase db, AppExecutors executors) {
        this.context = context.getApplicationContext();
        this.plantDao = db.plantDao();
        this.executor = executors.single();
        this.scheduler = new de.omagh.core_infra.sync.SyncScheduler(this.context);
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

    public List<Plant> getAllPlantsSync() {
        List<de.omagh.core_data.model.Plant> entities = plantDao.getAllSync();
        if (entities == null) return null;
        java.util.List<Plant> result = new java.util.ArrayList<>();
        for (de.omagh.core_data.model.Plant p : entities) {
            result.add(toDomain(p));
        }
        return result;
    }

    public LiveData<Plant> getPlant(String id) {
        return Transformations.map(plantDao.getById(id), this::toDomain);
    }

    public void insertPlant(Plant plant) {
        executor.execute(() -> plantDao.insert(toEntity(plant)));
        scheduler.scheduleDaily();
    }

    public void updatePlant(Plant plant) {
        executor.execute(() -> plantDao.update(toEntity(plant)));
        scheduler.scheduleDaily();
    }

    public void deletePlant(Plant plant) {
        executor.execute(() -> plantDao.delete(toEntity(plant)));
        scheduler.scheduleDaily();
    }

    private Plant toDomain(de.omagh.core_data.model.Plant entity) {
        if (entity == null) return null;
        return new Plant(entity.getId(), entity.getName(), entity.getType(), entity.getImageUri(), entity.getUpdatedAt());
    }

    private de.omagh.core_data.model.Plant toEntity(Plant plant) {
        return new de.omagh.core_data.model.Plant(
                plant.getId(),
                plant.getName(),
                plant.getType(),
                plant.getImageUri(),
                plant.getUpdatedAt()
        );
    }
}

