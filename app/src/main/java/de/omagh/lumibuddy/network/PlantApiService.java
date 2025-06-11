package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.lumibuddy.data.model.Plant;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit interface for the remote Plant Database API.
 */
public interface PlantApiService {
    /**
     * Returns a list of supported plant species.
     */
    @GET("plants")
    Call<List<Plant>> getPlants();

    /**
     * Returns a detailed care profile for a plant by id.
     */
    @GET("plants/{id}")
    Call<Plant> getPlant(@Path("id") String id);

    // Future: image-based identification
}