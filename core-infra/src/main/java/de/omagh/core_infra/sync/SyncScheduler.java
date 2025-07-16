package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Schedules periodic background sync using WorkManager.
 */
public class SyncScheduler {
    private final Context context;

    public SyncScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDaily() {
        PeriodicWorkRequest full = new PeriodicWorkRequest.Builder(
                FullSyncWorker.class,
                1, TimeUnit.DAYS)
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
