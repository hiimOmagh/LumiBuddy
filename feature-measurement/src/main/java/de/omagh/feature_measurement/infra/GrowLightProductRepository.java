package de.omagh.feature_measurement.infra;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.GrowLightProductDao;
import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.core_infra.network.GrowLightApiService;
import de.omagh.core_infra.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for retrieving grow light product information from PlantLightDB or DLC QPL.
 * Results are cached in Room for offline access.
 */
public class GrowLightProductRepository {
    private final GrowLightApiService apiService;
    private final GrowLightProductDao productDao;
    private final ExecutorService executor;
    private final String apiKey = "YOUR_API_KEY";

    public GrowLightProductRepository(Context context) {
        this(RetrofitClient.getInstance().create(GrowLightApiService.class),
                AppDatabase.getInstance(context.getApplicationContext()).growLightProductDao(),
                Executors.newSingleThreadExecutor());
    }

    // Constructor for tests
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
        executor.execute(() -> {
            try {
                Call<List<GrowLightProduct>> call = apiService.searchLamps(query, apiKey);
                Response<List<GrowLightProduct>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<GrowLightProduct> products = resp.body();
                    productDao.insertAll(products);
                    liveData.postValue(products);
                    return;
                }
            } catch (Exception ignored) {
            }
            List<GrowLightProduct> cached = productDao.search('%' + query + '%');
            liveData.postValue(cached);
        });
        return liveData;
    }
}