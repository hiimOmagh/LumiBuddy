package de.omagh.feature_measurement.infra;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.GrowLightDao;
import de.omagh.core_data.model.GrowLightProfile;
import de.omagh.core_infra.network.GrowLightApiService;
import de.omagh.core_infra.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
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
        Call<List<GrowLightProfile>> call = api.getLamps();
        call.enqueue(new Callback<List<GrowLightProfile>>() {
            @Override
            public void onResponse(Call<List<GrowLightProfile>> call, Response<List<GrowLightProfile>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> dao.insertAll(response.body()));
                }
            }

            @Override
            public void onFailure(Call<List<GrowLightProfile>> call, Throwable t) {
                Timber.tag("GrowLightSync").e(t, "sync error");
                // retain existing DAO data on failure
            }
        });
    }
}
