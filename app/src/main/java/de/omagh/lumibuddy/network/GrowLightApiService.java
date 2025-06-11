package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.lumibuddy.data.model.GrowLightProfile;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Retrofit interface for the remote Grow Light database.
 */
public interface GrowLightApiService {
    /**
     * List of supported grow lights.
     */
    @GET("lamps")
    Call<List<GrowLightProfile>> getLamps();

    /**
     * Details for a specific lamp by id.
     */
    @GET("lamps/{id}")
    Call<GrowLightProfile> getLamp(@Path("id") String id);
}