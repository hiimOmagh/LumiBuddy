package de.omagh.core_infra.network.lampid;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Retrofit service for remote lamp identification.
 */
public interface LampIdService {
    @POST("lampid/identify")
    Call<LampIdResponse> identify(@Body LampIdRequest request);
}
