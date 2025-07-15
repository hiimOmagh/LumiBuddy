package de.omagh.core_infra.network.plantid;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit interface for Plant.id photo identification.
 */
public interface PlantIdService {
    @POST("v3/identify")
    Call<PlantIdResponse> identify(@Body PlantIdRequest request);
}
