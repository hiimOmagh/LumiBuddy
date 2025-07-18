package de.omagh.feature_diary.di;

/**
 * Provides database DAOs and repositories for the Diary feature.
 * This module works in conjunction with {@link DiaryComponent}.
 */

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.db.TaskDao;
/*
import de.omagh.core_data.repository.DiaryRepository;
*/
import de.omagh.core_data.repository.TaskRepository;
import de.omagh.core_domain.util.AppExecutors;

@Module
public class DiaryModule {
    @Provides
    DiaryDao provideDiaryDao(AppDatabase db) {
        return db.diaryDao();
    }

    @Provides
    TaskDao provideTaskDao(AppDatabase db) {
        return db.taskDao();
    }

    @Provides
    TaskRepository provideTaskRepository(TaskDao dao, AppExecutors executors) {
        return new TaskRepository(dao, executors);
    }
}
