package de.omagh.feature_measurement.infra;

import androidx.lifecycle.LiveData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.GrowLightDao;
import de.omagh.core_data.model.GrowLightProfile;
import de.omagh.core_infra.network.GrowLightApiService;
import de.omagh.core_infra.network.RetrofitClient;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Syncs grow light data from the API to the local DB.
 */
public class GrowLightSyncManager {
    private final GrowLightDao dao;
    private final GrowLightApiService api;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GrowLightSyncManager(AppDatabase db) {
        this.dao = db.growLightDao();
        this.api = RetrofitClient.getInstance().create(GrowLightApiService.class);
    }

    public LiveData<List<GrowLightProfile>> getGrowLights() {
        return dao.getAll();
    }

    /**
     * Fetches lamp specs from the API and persists them.
     */
    public void syncLamps() {
        executor.execute(() -> {
            try {
                Response<List<GrowLightProfile>> resp = api.getLamps().execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    dao.insertAll(resp.body());
                }
            } catch (IOException e) {
                Timber.tag("GrowLightSync").e(e, "sync error");
            }
        });
    }
}