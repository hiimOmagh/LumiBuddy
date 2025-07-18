package de.omagh.core_infra.di;

/**
 * Provides networking instances such as the shared {@link Retrofit} client used
 * across the app.
 */

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;

import javax.inject.Singleton;
import retrofit2.converter.gson.GsonConverterFactory;
import de.omagh.core_domain.Config;

@Module
public class NetworkModule {
    @Provides
    @Singleton
    Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
