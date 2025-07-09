package de.omagh.core_infra.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import de.omagh.core_infra.BuildConfig;

/**
 * Simple Retrofit client singleton for network calls.
 */
public class RetrofitClient {
    private static final String BASE_URL = BuildConfig.BASE_URL;

    private RetrofitClient() {
    }

    public static Retrofit getInstance() {
        return InstanceHolder.instance;
    }

    private static final class InstanceHolder {
        private static final Retrofit instance = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}