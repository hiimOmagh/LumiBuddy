package de.omagh.core_infra.recommendation;

import android.content.Context;

import de.omagh.core_infra.util.NotificationPermissionHelper;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

/**
 * Schedules {@link WateringWorker} using WorkManager.
 */
public class WateringWorkScheduler {
    private final Context context;

    public WateringWorkScheduler(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scheduleDaily() {
        if (!NotificationPermissionHelper.hasPermission(context)) {
            return; // Permission not granted; don't schedule
        }
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                WateringWorker.class,
                1,
                TimeUnit.DAYS)
                .build();
        WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                        WateringWorker.UNIQUE_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        request);
    }
}
