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
import retrofit2.Callback;
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
        Call<List<PlantSpeciesEntity>> call = apiService.searchSpecies(query, apiKey);
        call.enqueue(new Callback<List<PlantSpeciesEntity>>() {
            @Override
            public void onResponse(Call<List<PlantSpeciesEntity>> call, Response<List<PlantSpeciesEntity>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PlantSpeciesEntity> species = response.body();
                    executor.execute(() -> {
                        speciesDao.insertAll(species);
                        liveData.postValue(mapSpeciesList(species));
                    });
                } else {
                    loadFromCache();
                }
            }

            @Override
            public void onFailure(Call<List<PlantSpeciesEntity>> call, Throwable t) {
                loadFromCache();
            }

            private void loadFromCache() {
                executor.execute(() -> {
                    List<PlantSpeciesEntity> cached = speciesDao.search('%' + query + '%');
                    liveData.postValue(mapSpeciesList(cached));
                });
            }
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
            } else {
                Call<List<PlantCareProfileEntity>> call = apiService.getCareProfile(speciesId, apiKey);
                call.enqueue(new Callback<List<PlantCareProfileEntity>>() {
                    @Override
                    public void onResponse(Call<List<PlantCareProfileEntity>> call, Response<List<PlantCareProfileEntity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<PlantCareProfileEntity> profiles = response.body();
                            for (PlantCareProfileEntity p : profiles) p.setLocalId(0);
                            executor.execute(() -> {
                                profileDao.insertAll(profiles);
                                liveData.postValue(mapProfileList(profiles));
                            });
                        } else {
                            liveData.postValue(Collections.emptyList());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlantCareProfileEntity>> call, Throwable t) {
                        liveData.postValue(Collections.emptyList());
                    }
                });
            }
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
                Call<List<PlantSpeciesEntity>> call = idService.identify(part, idApiKey);
                call.enqueue(new Callback<List<PlantSpeciesEntity>>() {
                    @Override
                    public void onResponse(Call<List<PlantSpeciesEntity>> call, Response<List<PlantSpeciesEntity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            liveData.postValue(mapSpeciesList(response.body()));
                        } else {
                            liveData.postValue(Collections.emptyList());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<PlantSpeciesEntity>> call, Throwable t) {
                        liveData.postValue(Collections.emptyList());
                    }
                });
            } catch (Exception ignored) {
                liveData.postValue(Collections.emptyList());
            }
        });
        return liveData;
    }
}
