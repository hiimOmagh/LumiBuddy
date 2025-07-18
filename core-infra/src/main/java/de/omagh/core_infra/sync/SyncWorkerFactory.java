package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.ListenableWorker;
import androidx.work.WorkerFactory;
import androidx.work.WorkerParameters;

import javax.inject.Provider;

import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * WorkerFactory that injects repositories into sync workers.
 */
public class SyncWorkerFactory extends WorkerFactory {
    private final Provider<FirebaseManager> firebaseManagerProvider;

    public SyncWorkerFactory(Provider<FirebaseManager> firebaseManagerProvider) {
        this.firebaseManagerProvider = firebaseManagerProvider;
    }

    @Nullable
    @Override
    public ListenableWorker createWorker(@NonNull Context context,
                                         @NonNull String workerClassName,
                                         @NonNull WorkerParameters workerParameters) {
        if (workerClassName.equals(UploadPlantWorker.class.getName())) {
            return new UploadPlantWorker(context, workerParameters, firebaseManagerProvider.get());
        } else if (workerClassName.equals(UploadDiaryEntryWorker.class.getName())) {
            return new UploadDiaryEntryWorker(context, workerParameters, firebaseManagerProvider.get());
        }
        return null;
    }
}
