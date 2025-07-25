package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * WorkManager based implementation of {@link de.omagh.core_domain.sync.SyncScheduler}
 * that enqueues periodic background sync tasks.
 */
public class WorkSyncScheduler implements de.omagh.core_domain.sync.SyncScheduler {
    private final Context context;

    public WorkSyncScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public void scheduleDaily() {
        Constraints netConnected = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest full = new PeriodicWorkRequest.Builder(
                FullSyncWorker.class,
                1, TimeUnit.DAYS)
                .setConstraints(netConnected)
                .build();
        PeriodicWorkRequest backup = new PeriodicWorkRequest.Builder(
                BackupWorker.class,
                1, TimeUnit.DAYS)
                .build();
        WorkManager wm = WorkManager.getInstance(context);
        wm.enqueueUniquePeriodicWork(
                "FullSyncWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                full);
        wm.enqueueUniquePeriodicWork(
                "BackupWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                backup);
    }
}
