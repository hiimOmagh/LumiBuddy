package de.omagh.feature_growschedule.di;

import android.app.Application;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.recommendation.NotificationManager;
import de.omagh.core_infra.recommendation.RecommendationEngine;
import de.omagh.core_infra.recommendation.WateringScheduler;
import de.omagh.feature_growschedule.ui.HomeViewModel;
import de.omagh.feature_growschedule.ui.HomeViewModelFactory;

import javax.inject.Provider;

@Module
public abstract class GrowScheduleModule {
    @Provides
    static PlantRepository providePlantRepository(AppDatabase db, AppExecutors executors) {
        return new PlantRepository(db, executors);
    }

    @Provides
    static DiaryRepository provideDiaryRepository(AppDatabase db, AppExecutors executors) {
        return new DiaryRepository(db.diaryDao(), executors);
    }

    @Provides
    static RecommendationEngine provideRecommendationEngine() {
        return new RecommendationEngine();
    }

    @Provides
    static WateringScheduler provideWateringScheduler(RecommendationEngine engine,
                                                      Application app,
                                                      AppDatabase db) {
        return new WateringScheduler(engine,
                new NotificationManager(app.getApplicationContext()),
                db.diaryDao());
    }

    @Provides
    static HomeViewModelFactory provideViewModelFactory(Provider<HomeViewModel> provider) {
        return new HomeViewModelFactory(provider);
    }

    @Binds
    abstract PlantDataSource bindPlantRepository(PlantRepository repo);

    @Binds
    abstract DiaryDataSource bindDiaryRepository(DiaryRepository repo);
}
