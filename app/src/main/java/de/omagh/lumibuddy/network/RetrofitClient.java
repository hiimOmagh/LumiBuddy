package de.omagh.lumibuddy.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Simple Retrofit client singleton for network calls.
 */
public class RetrofitClient {
    private static final String BASE_URL = "https://example.com/";
    private static Retrofit instance;

    private RetrofitClient() {
    }

    public static Retrofit getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return instance;
    }
}