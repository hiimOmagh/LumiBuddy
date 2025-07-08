package de.omagh.core_infra.recommendation;

import android.Manifest;
import android.content.Context;

import androidx.annotation.RequiresPermission;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Schedules periodic background scans using {@link WorkManager}.
 */
public class PeriodicScanScheduler {

    private final Context context;

    public PeriodicScanScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Enqueues the {@link PeriodicScanWorker} to run once per week.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void scheduleWeekly() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                PeriodicScanWorker.class,
                7,
                TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        "PeriodicScanWorker",
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request);
    }
}