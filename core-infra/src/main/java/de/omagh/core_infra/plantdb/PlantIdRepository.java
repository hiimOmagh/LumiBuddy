package de.omagh.core_infra.plantdb;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

import de.omagh.core_domain.util.AppExecutors;

import de.omagh.core_infra.network.plantid.PlantIdInterceptor;
import de.omagh.core_infra.network.plantid.PlantIdRequest;
import de.omagh.core_infra.network.plantid.PlantIdResponse;
import de.omagh.core_infra.network.plantid.PlantIdService;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.BuildConfig;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Repository wrapping Plant.id API for photo identification.
 */
public class PlantIdRepository {
    private static final String BASE_URL = "https://api.plant.id/";
    private static final String API_KEY = BuildConfig.PLANT_ID_API_KEY;
    private final PlantIdService service;
    private final ExecutorService executor;

    public PlantIdRepository(AppExecutors executors) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new PlantIdInterceptor(API_KEY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(PlantIdService.class);
        this.executor = executors.single();
    }

    // Constructor for tests
    public PlantIdRepository(PlantIdService service, ExecutorService executor) {
        this.service = service;
        this.executor = executor;
    }

    /**
     * Identify a plant from an image bitmap.
     */
    public LiveData<PlantIdSuggestion> identifyPlant(Bitmap bitmap) {
        MutableLiveData<PlantIdSuggestion> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                PlantIdRequest request = new PlantIdRequest(Collections.singletonList(base64), false);
                Call<PlantIdResponse> call = service.identify(request);
                Response<PlantIdResponse> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null && !resp.body().getSuggestions().isEmpty()) {
                    PlantIdResponse.Suggestion best = resp.body().getSuggestions().get(0);
                    String sci = best.getName();
                    String common = sci;
                    if (best.getPlantDetails() != null && best.getPlantDetails().getCommonNames() != null
                            && !best.getPlantDetails().getCommonNames().isEmpty()) {
                        common = best.getPlantDetails().getCommonNames().get(0);
                    }
                    liveData.postValue(new PlantIdSuggestion(common, sci));
                    return;
                }
            } catch (Exception ignored) {
            }
            liveData.postValue(null);
        });
        return liveData;
    }
}