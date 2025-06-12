package de.omagh.lumibuddy.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Simple Retrofit client singleton for network calls.
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://example.com/";

    private RetrofitClient() {
    }

    private static final class InstanceHolder {
        private static final Retrofit instance = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static Retrofit getInstance() {
        return InstanceHolder.instance;
    }
}