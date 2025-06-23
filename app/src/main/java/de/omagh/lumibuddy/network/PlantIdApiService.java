package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.core_domain.model.PlantSpecies;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Retrofit service for image based plant identification using Plant.id API.
 */
public interface PlantIdApiService {

    @Multipart
    @POST("identify")
    Call<List<PlantSpecies>> identify(@Part MultipartBody.Part image, @Query("api_key") String apiKey);
}