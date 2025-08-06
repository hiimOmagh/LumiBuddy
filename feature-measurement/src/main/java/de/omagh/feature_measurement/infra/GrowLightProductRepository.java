package de.omagh.feature_measurement.infra;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import de.omagh.core_data.db.GrowLightProductDao;
import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.core_infra.network.GrowLightApiService;
import de.omagh.feature_measurement.BuildConfig;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository for retrieving grow light product information from PlantLightDB or DLC QPL.
 * Results are cached in Room for offline access.
 */
public class GrowLightProductRepository {
    private final GrowLightApiService apiService;
    private final GrowLightProductDao productDao;
    private final ExecutorService executor;
    private final String apiKey = BuildConfig.GROW_LIGHT_API_KEY;

    @Inject
    public GrowLightProductRepository(GrowLightApiService apiService,
                                      GrowLightProductDao productDao,
                                      ExecutorService executor) {
        this.apiService = apiService;
        this.productDao = productDao;
        this.executor = executor;
    }

    /**
     * Search grow light products by keyword.
     */
    public LiveData<List<GrowLightProduct>> searchGrowLights(String query) {
        MutableLiveData<List<GrowLightProduct>> liveData = new MutableLiveData<>();
        Call<List<GrowLightProduct>> call = apiService.searchLamps(query, apiKey);
        call.enqueue(new Callback<List<GrowLightProduct>>() {
            @Override
            public void onResponse(Call<List<GrowLightProduct>> call, Response<List<GrowLightProduct>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<GrowLightProduct> products = response.body();
                    executor.execute(() -> productDao.insertAll(products));
                    liveData.postValue(products);
                } else {
                    loadFromCache();
                }
            }
            @Override
            public void onFailure(Call<List<GrowLightProduct>> call, Throwable t) {
                loadFromCache();
            }

            private void loadFromCache() {
                executor.execute(() -> {
                    List<GrowLightProduct> cached = productDao.search('%' + query + '%');
                    liveData.postValue(cached);
                });
            }
        });
        return liveData;
    }
}