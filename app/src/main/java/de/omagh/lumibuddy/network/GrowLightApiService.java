package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.lumibuddy.data.model.GrowLightProduct;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit interface for the remote Grow Light database.
 */
public interface GrowLightApiService {
    /**
     * List of supported grow lights.
     */
    @GET("lamps")
    Call<List<GrowLightProduct>> searchLamps(@Query("q") String query, @Query("token") String apiKey);
    /**
     * Details for a specific lamp by id.
     */
    @GET("lamps/{id}")
    Call<GrowLightProduct> getLamp(@Path("id") String id, @Query("token") String apiKey);
}