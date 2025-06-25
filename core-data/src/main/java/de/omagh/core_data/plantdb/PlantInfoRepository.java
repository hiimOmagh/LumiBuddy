package de.omagh.core_data.plantdb;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantCareProfileDao;
import de.omagh.core_data.db.PlantSpeciesDao;
import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import de.omagh.core_infra.network.PlantApiService;
import de.omagh.core_infra.network.PlantIdApiService;
import de.omagh.core_infra.network.RetrofitClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Repository providing plant species search, detailed care profiles and image based identification.
 * Uses a network first strategy with Room caching for offline usage.
 */
public class PlantInfoRepository {
    private final PlantApiService apiService;
    private final PlantIdApiService idService;
    private final PlantSpeciesDao speciesDao;
    private final PlantCareProfileDao profileDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String apiKey = "YOUR_API_KEY"; // documented in README
    private final String idApiKey = "YOUR_PLANT_ID_KEY";

    public PlantInfoRepository(Context context) {
        apiService = RetrofitClient.getInstance().create(PlantApiService.class);
        idService = RetrofitClient.getInstance().create(PlantIdApiService.class);
        AppDatabase db = AppDatabase.getInstance(context.getApplicationContext());
        speciesDao = db.plantSpeciesDao();
        profileDao = db.plantCareProfileDao();
    }

    /**
     * Search species from remote API, caching results locally.
     */
    public LiveData<List<PlantSpecies>> searchSpecies(String query) {
        MutableLiveData<List<PlantSpecies>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                Call<List<PlantSpecies>> call = apiService.searchSpecies(query, apiKey);
                Response<List<PlantSpecies>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PlantSpecies> species = resp.body();
                    speciesDao.insertAll(species);
                    liveData.postValue(species);
                    return;
                }
            } catch (Exception ignored) {
            }
            List<PlantSpecies> cached = speciesDao.search('%' + query + '%');
            liveData.postValue(cached);
        });
        return liveData;
    }

    /**
     * Retrieve a full care profile for the given species id.
     */
    public LiveData<List<PlantCareProfileEntity>> getCareProfile(String speciesId) {
        MutableLiveData<List<PlantCareProfileEntity>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            List<PlantCareProfileEntity> cached = profileDao.getBySpecies(speciesId);
            if (!cached.isEmpty()) {
                liveData.postValue(cached);
                return;
            }
            try {
                Call<List<PlantCareProfileEntity>> call = apiService.getCareProfile(speciesId, apiKey);
                Response<List<PlantCareProfileEntity>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PlantCareProfileEntity> profiles = resp.body();
                    for (PlantCareProfileEntity p : profiles) p.setLocalId(0);
                    profileDao.insertAll(profiles);
                    liveData.postValue(profiles);
                    return;
                }
            } catch (Exception ignored) {
            }
            liveData.postValue(Collections.emptyList());
        });
        return liveData;
    }

    /**
     * Attempts to identify a plant by common or scientific name.
     */
    public LiveData<List<PlantSpecies>> identifyPlantFromImage(File image) {
        MutableLiveData<List<PlantSpecies>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                RequestBody req = RequestBody.create(MediaType.parse("image/*"), image);
                MultipartBody.Part part = MultipartBody.Part.createFormData("image", image.getName(), req);
                Response<List<PlantSpecies>> resp = idService.identify(part, idApiKey).execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    liveData.postValue(resp.body());
                    return;
                }
            } catch (Exception ignored) {
            }
            liveData.postValue(Collections.emptyList());
        });
        return liveData;
    }
}