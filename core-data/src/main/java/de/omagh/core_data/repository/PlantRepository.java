package de.omagh.core_data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
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
 * {@link de.omagh.core_domain.sync.SyncScheduler}.
 */
public class PlantRepository implements PlantDataSource {
    private static final String TAG = "PlantRepository";
    private final PlantDao plantDao;
    private final ExecutorService executor;
    private final de.omagh.core_domain.sync.SyncScheduler scheduler;
    private final Context context;

    public PlantRepository(Context context,
                           AppDatabase db,
                           AppExecutors executors,
                           de.omagh.core_domain.sync.SyncScheduler scheduler) {
        this.context = context.getApplicationContext();
        this.plantDao = db.plantDao();
        this.executor = executors.single();
        this.scheduler = scheduler;
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

    public Result<List<Plant>> getAllPlantsSync() {
        try {
            List<de.omagh.core_data.model.Plant> entities = plantDao.getAllSync();
            if (entities == null) {
                return Result.success(null);
            }
            java.util.List<Plant> result = new java.util.ArrayList<>();
            for (de.omagh.core_data.model.Plant p : entities) {
                result.add(toDomain(p));
            }
            return Result.success(result);
        } catch (Exception e) {
            Log.e(TAG, "getAllPlantsSync failed", e);
            return Result.error(null, e);
        }
    }

    public LiveData<Plant> getPlant(String id) {
        return Transformations.map(plantDao.getById(id), this::toDomain);
    }

    public LiveData<Result<Void>> insertPlant(Plant plant) {
        MutableLiveData<Result<Void>> live = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                plantDao.insert(toEntity(plant));
                scheduler.scheduleDaily();
                live.postValue(Result.success(null));
            } catch (Exception e) {
                Log.e(TAG, "insertPlant failed", e);
                live.postValue(Result.error(null, e));
            }
        });
        return live;
    }

    public LiveData<Result<Void>> updatePlant(Plant plant) {
        MutableLiveData<Result<Void>> live = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                plantDao.update(toEntity(plant));
                scheduler.scheduleDaily();
                live.postValue(Result.success(null));
            } catch (Exception e) {
                Log.e(TAG, "updatePlant failed", e);
                live.postValue(Result.error(null, e));
            }
        });
        return live;
    }

    public LiveData<Result<Void>> deletePlant(Plant plant) {
        MutableLiveData<Result<Void>> live = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                plantDao.delete(toEntity(plant));
                scheduler.scheduleDaily();
                live.postValue(Result.success(null));
            } catch (Exception e) {
                Log.e(TAG, "deletePlant failed", e);
                live.postValue(Result.error(null, e));
            }
        });
        return live;
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

