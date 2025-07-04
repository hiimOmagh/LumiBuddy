package de.omagh.feature_diary.di;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.di.FeatureScope;

@Module
public class DiaryModule {
    @Provides
    @FeatureScope
    DiaryDao provideDiaryDao(AppDatabase db) {
        return db.diaryDao();
    }

    @Provides
    @FeatureScope
    DiaryRepository provideDiaryRepository(DiaryDao dao) {
        return new DiaryRepository(dao);
    }
}