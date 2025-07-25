package de.omagh.core_infra.network;

import java.util.List;

import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.core_data.model.GrowLightProfile;
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
     * Retrieve the list of lamp profiles without filtering.
     */
    @GET("lamps")
    Call<List<GrowLightProfile>> getLamps();

    /**
     * Details for a specific lamp by id.
     */
    @GET("lamps/{id}")
    Call<GrowLightProduct> getLamp(@Path("id") String id, @Query("token") String apiKey);
}