package de.omagh.core_infra.plantdb;

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
import de.omagh.core_data.model.PlantSpeciesEntity;
import de.omagh.core_domain.model.PlantCareProfile;
import de.omagh.core_domain.model.PlantSpecies;
import de.omagh.core_infra.BuildConfig;
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
    private final ExecutorService executor;
    private final String apiKey = BuildConfig.PLANT_API_KEY;
    private final String idApiKey = BuildConfig.PLANT_ID_API_KEY;

    public PlantInfoRepository(Context context) {
        this(RetrofitClient.getInstance().create(PlantApiService.class),
                RetrofitClient.getInstance().create(PlantIdApiService.class),
                AppDatabase.getInstance(context.getApplicationContext()).plantSpeciesDao(),
                AppDatabase.getInstance(context.getApplicationContext()).plantCareProfileDao(),
                Executors.newSingleThreadExecutor());
    }

    // Constructor for tests
    public PlantInfoRepository(PlantApiService apiService,
                               PlantIdApiService idService,
                               PlantSpeciesDao speciesDao,
                               PlantCareProfileDao profileDao,
                               ExecutorService executor) {
        this.apiService = apiService;
        this.idService = idService;
        this.speciesDao = speciesDao;
        this.profileDao = profileDao;
        this.executor = executor;
    }

    private PlantSpecies mapSpecies(PlantSpeciesEntity e) {
        return new PlantSpecies(e.getId(), e.getScientificName(), e.getCommonName(), e.getImageUrl());
    }

    private PlantCareProfile mapProfile(PlantCareProfileEntity e) {
        return new PlantCareProfile(
                de.omagh.core_domain.model.PlantStage.valueOf(e.getStage()),
                0f,
                0f,
                0f,
                0f,
                e.getWateringIntervalDays(),
                e.getMinHumidity(),
                e.getMaxHumidity(),
                e.getMinTemperature(),
                e.getMaxTemperature()
        );
    }

    private java.util.List<PlantSpecies> mapSpeciesList(java.util.List<PlantSpeciesEntity> list) {
        java.util.List<PlantSpecies> out = new java.util.ArrayList<>();
        for (PlantSpeciesEntity e : list) { out.add(mapSpecies(e)); }
        return out;
    }

    private java.util.List<PlantCareProfile> mapProfileList(java.util.List<PlantCareProfileEntity> list) {
        java.util.List<PlantCareProfile> out = new java.util.ArrayList<>();
        for (PlantCareProfileEntity e : list) { out.add(mapProfile(e)); }
        return out;
    }

    /**
     * Search species from remote API, caching results locally.
     */
    public LiveData<List<PlantSpeciesEntity>> searchSpecies(String query) {
        MutableLiveData<List<PlantSpeciesEntity>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                Call<List<PlantSpeciesEntity>> call = apiService.searchSpecies(query, apiKey);
                Response<List<PlantSpeciesEntity>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PlantSpeciesEntity> species = resp.body();
                    speciesDao.insertAll(species);
                    liveData.postValue(mapSpeciesList(species));
                    return;
                }
            } catch (Exception ignored) {
            }
            List<PlantSpeciesEntity> cached = speciesDao.search('%' + query + '%');
            liveData.postValue(mapSpeciesList(cached));
        });
        return liveData;
    }

    /**
     * Retrieve a full care profile for the given species id.
     */
    public LiveData<List<PlantCareProfile>> getCareProfile(String speciesId) {
        MutableLiveData<List<PlantCareProfile>> liveData = new MutableLiveData<>();
        executor.execute(() -> {
            List<PlantCareProfileEntity> cached = profileDao.getBySpecies(speciesId);
            if (!cached.isEmpty()) {
                liveData.postValue(mapProfileList(cached));
                return;
            }
            try {
                Call<List<PlantCareProfileEntity>> call = apiService.getCareProfile(speciesId, apiKey);
                Response<List<PlantCareProfileEntity>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    List<PlantCareProfileEntity> profiles = resp.body();
                    for (PlantCareProfileEntity p : profiles) p.setLocalId(0);
                    profileDao.insertAll(profiles);
                    liveData.postValue(mapProfileList(profiles));
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
                Response<List<PlantSpeciesEntity>> resp = idService.identify(part, idApiKey).execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    liveData.postValue(mapSpeciesList(resp.body()));
                    return;
                }
            } catch (Exception ignored) {
            }
            liveData.postValue(Collections.emptyList());
        });
        return liveData;
    }
}
