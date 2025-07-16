package de.omagh.core_infra.recommendation;

import android.content.Context;

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
