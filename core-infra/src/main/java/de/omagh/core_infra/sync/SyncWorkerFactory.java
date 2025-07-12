package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import javax.inject.Provider;

import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.PlantDataSource;

/**
 * WorkerFactory that injects repositories into sync workers.
 */
public class SyncWorkerFactory extends WorkerFactory {
    private final Provider<PlantDataSource> plantRepoProvider;
    private final Provider<DiaryDataSource> diaryRepoProvider;

    public SyncWorkerFactory(Provider<PlantDataSource> plantRepoProvider,
                             Provider<DiaryDataSource> diaryRepoProvider) {
        this.plantRepoProvider = plantRepoProvider;
        this.diaryRepoProvider = diaryRepoProvider;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context context,
                                         @NonNull String workerClassName,
                                         @NonNull WorkerParameters workerParameters) {
        if (workerClassName.equals(UploadPlantWorker.class.getName())) {
            return new UploadPlantWorker(context, workerParameters, plantRepoProvider.get());
        } else if (workerClassName.equals(UploadDiaryEntryWorker.class.getName())) {
            return new UploadDiaryEntryWorker(context, workerParameters, diaryRepoProvider.get());
        }
        return null;
    }
}
