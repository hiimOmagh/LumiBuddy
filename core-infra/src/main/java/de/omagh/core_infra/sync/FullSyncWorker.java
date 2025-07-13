package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;

/**
 * Worker that performs full plant and diary sync.
 */
public class FullSyncWorker extends Worker {
    private final PlantSyncManager plantSyncManager;
    private final DiarySyncManager diarySyncManager;

    public FullSyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        plantSyncManager = core.plantSyncManager();
        diarySyncManager = core.diarySyncManager();
    }

    @NonNull
    @Override
    public Result doWork() {
        plantSyncManager.sync();
        diarySyncManager.sync();
        return Result.success();
    }
}
