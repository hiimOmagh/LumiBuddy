package de.omagh.core_infra.recommendation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.sync.WorkSyncScheduler;

/**
 * Worker that triggers {@link WateringScheduler} via WorkManager.
 */
public class WateringWorker extends Worker {
    public static final String UNIQUE_NAME = "WateringWorker";

    private final WateringScheduler scheduler;
    private final PlantRepository plantRepository;

    public WateringWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        AppDatabase db = AppDatabase.getInstance(context);
        plantRepository = new PlantRepository(context, db, core.appExecutors(), new WorkSyncScheduler(context));
        scheduler = new WateringScheduler(new RecommendationEngine(), new NotificationManager(context), db.diaryDao());
    }

    @NonNull
    @Override
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public Result doWork() {
        Context ctx = getApplicationContext();
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }
        de.omagh.core_data.repository.Result<List<Plant>> res = plantRepository.getAllPlantsSync();
        List<Plant> plants = res.getData();
        if (!res.isSuccess()) {
            android.util.Log.e("WateringWorker", "getAllPlantsSync failed", res.getError());
        }
        if (plants == null) plants = new ArrayList<>();
        scheduler.runDailyCheck(plants);
        return Result.success();
    }
}
