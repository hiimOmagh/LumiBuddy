package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Simple worker that triggers cloud backup for plants and diary entries.
 */
public class BackupWorker extends Worker {
    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        new PlantSyncManager().syncToCloud(java.util.Collections.emptyList());
        new DiarySyncManager().syncToCloud();
        return Result.success();
    }
}