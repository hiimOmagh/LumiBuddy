package de.omagh.core_data.repository.firebase;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.Result;
import de.omagh.core_domain.model.Plant;

/**
 * Remote {@link PlantDataSource} implementation backed by Firebase Firestore.
 */
public class FirebasePlantRepository implements PlantDataSource {
    private static final String TAG = "FirebasePlantRepo";
    private final FirestorePlantDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FirebasePlantRepository(String uid) {
        this.dao = new FirestorePlantDao(uid);
    }

    public LiveData<List<Plant>> getAllPlants() {
        return dao.getAll();
    }

    public Result<List<Plant>> getAllPlantsSync() {
        try {
            return Result.success(dao.getAllSync());
        } catch (Exception e) {
            Log.e(TAG, "getAllPlantsSync failed", e);
            return Result.error(null, e);
        }
    }

    public LiveData<Plant> getPlant(String id) {
        return dao.getById(id);
    }

    public LiveData<Result<Void>> insertPlant(Plant plant) {
        MutableLiveData<Result<Void>> live = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                dao.insert(plant);
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
                dao.update(plant);
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
                dao.delete(plant);
                live.postValue(Result.success(null));
            } catch (Exception e) {
                Log.e(TAG, "deletePlant failed", e);
                live.postValue(Result.error(null, e));
            }
        });
        return live;
    }
}
