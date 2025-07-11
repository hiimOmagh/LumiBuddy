package de.omagh.feature_diary.di;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_domain.util.AppExecutors;

@Module
public class DiaryModule {
    @Provides
    DiaryDao provideDiaryDao(AppDatabase db) {
        return db.diaryDao();
    }

    @Provides
    DiaryRepository provideDiaryRepository(DiaryDao dao, AppExecutors executors) {
        return new DiaryRepository(dao, executors);
    }
}
