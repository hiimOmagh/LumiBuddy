package de.omagh.lumibuddy.network.plantid;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor adding the Plant.id API key header.
 */
public class PlantIdInterceptor implements Interceptor {
    private final String apiKey;

    public PlantIdInterceptor(String apiKey) {
        this.apiKey = apiKey;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Api-Key", apiKey)
                .build();
        return chain.proceed(request);
    }
}