package de.omagh.lumibuddy.feature_plantdb;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.lifecycle.LiveData;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.db.PlantDao;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.network.PlantApiService;
import de.omagh.lumibuddy.network.RetrofitClient;
import retrofit2.Response;

/**
 * Handles syncing plant data between the remote API and local Room DB.
 */
public class PlantSyncManager {
    private final PlantDao plantDao;
    private final PlantApiService api;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PlantSyncManager(AppDatabase db) {
        this.plantDao = db.plantDao();
        this.api = RetrofitClient.getInstance().create(PlantApiService.class);
    }

    public LiveData<List<Plant>> getPlants() {
        return plantDao.getAll();
    }

    /**
     * Fetches plant data from the API and stores it locally.
     */
    public void syncPlants() {
        executor.execute(() -> {
            try {
                Response<List<Plant>> resp = api.getPlants().execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    plantDao.insertAll(resp.body());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}