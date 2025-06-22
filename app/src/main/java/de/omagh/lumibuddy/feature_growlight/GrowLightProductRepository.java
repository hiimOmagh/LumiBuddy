package de.omagh.lumibuddy.feature_growlight;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.db.GrowLightProductDao;
import de.omagh.lumibuddy.data.model.GrowLightProduct;
import de.omagh.lumibuddy.network.GrowLightApiService;
import de.omagh.lumibuddy.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository for retrieving grow light product information from PlantLightDB or DLC QPL.
 * Results are cached in Room for offline access.
 */
public class GrowLightProductRepository {
    private final GrowLightApiService apiService;
    private final GrowLightProductDao productDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String apiKey = "YOUR_API_KEY";

    public GrowLightProductRepository(Context context) {
        apiService = RetrofitClient.getInstance().create(GrowLightApiService.class);
        productDao = AppDatabase.getInstance(context.getApplicationContext()).growLightProductDao();
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