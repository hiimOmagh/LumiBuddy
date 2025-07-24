package de.omagh.core_infra.recommendation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.sync.WorkSyncScheduler;

/**
 * Worker performing periodic care checks using {@link WorkManager}.
 */
public class PeriodicScanWorker extends Worker {

    private final WateringScheduler wateringScheduler;
    private final LightRecommendationScheduler lightScheduler;
    private final PlantRepository plantRepository;

    public PeriodicScanWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        AppExecutors executors = core.appExecutors();
        AppDatabase db = AppDatabase.getInstance(context);
        plantRepository = new PlantRepository(context, db, executors, new WorkSyncScheduler(context));
        DiaryDao diaryDao = db.diaryDao();
        RecommendationEngine engine = new RecommendationEngine();
        NotificationManager notifications = new NotificationManager(context);
        wateringScheduler = new WateringScheduler(engine, notifications, diaryDao);
        lightScheduler = new LightRecommendationScheduler(engine, notifications, diaryDao);
    }

    @NonNull
    @Override
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public Result doWork() {
        List<Plant> plants = plantRepository.getAllPlants().getValue();
        if (plants == null) {
            plants = new ArrayList<>();
        }
        Context context = getApplicationContext();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return Result.success();
        }
        wateringScheduler.runDailyCheck(plants);
        lightScheduler.runLightCheck(plants);
        return Result.success();
    }
}