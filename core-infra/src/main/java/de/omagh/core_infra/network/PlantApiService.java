package de.omagh.core_infra.network;

import java.util.List;

import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for the remote Plant Database API.
 */
public interface PlantApiService {

    /**
     * Search plant species from Trefle/Perenual APIs.
     * Requires API key passed via query parameter.
     */
    @GET("plants/search")
    Call<List<PlantSpecies>> searchSpecies(@Query("q") String query, @Query("token") String apiKey);

    /**
     * Fetch detailed care profile for the given species ID.
     */
    @GET("plants/{id}")
    Call<List<PlantCareProfileEntity>> getCareProfile(@Path("id") String id, @Query("token") String apiKey);
}