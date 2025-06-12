package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.lumibuddy.data.model.Plant;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Retrofit interface defining endpoints used for syncing a user's plant list
 * with a remote cloud backend.
 */
public interface PlantSyncService {

    /**
     * Upload the given plants to the cloud.
     */
    @POST("sync/plants")
    Call<Void> uploadPlants(@Body List<Plant> plants);

    /**
     * Download the user's plants from the cloud.
     */
    @GET("sync/plants")
    Call<List<Plant>> downloadPlants();
}