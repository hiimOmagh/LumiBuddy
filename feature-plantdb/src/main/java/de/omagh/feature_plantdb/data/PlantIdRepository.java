package de.omagh.feature_plantdb.data;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.network.plantid.PlantIdInterceptor;
import de.omagh.lumibuddy.network.plantid.PlantIdRequest;
import de.omagh.lumibuddy.network.plantid.PlantIdResponse;
import de.omagh.lumibuddy.network.plantid.PlantIdService;
import de.omagh.lumibuddy.network.plantid.PlantIdSuggestion;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Repository wrapping Plant.id API for photo identification.
 */
public class PlantIdRepository {
    private static final String BASE_URL = "https://api.plant.id/v2/";
    private static final String API_KEY = "YOUR_PLANT_ID_KEY";

    private final PlantIdService service;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public PlantIdRepository() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new PlantIdInterceptor(API_KEY))
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        service = retrofit.create(PlantIdService.class);
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
                PlantIdRequest request = new PlantIdRequest(Collections.singletonList(base64));
                Call<PlantIdResponse> call = service.identify(request);
                Response<PlantIdResponse> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null && !resp.body().getSuggestions().isEmpty()) {
                    PlantIdResponse.Suggestion best = resp.body().getSuggestions().get(0);
                    String sci = best.getPlantName();
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