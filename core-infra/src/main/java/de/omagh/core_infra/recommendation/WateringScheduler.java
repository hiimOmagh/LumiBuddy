package de.omagh.core_infra.recommendation;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.DiaryEntry;

/**
 * Performs a daily check for each plant and triggers notifications if watering is due.
 */
public class WateringScheduler {

    private final RecommendationEngine engine;
    private final NotificationManager notificationManager;
    private final DiaryDao diaryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WateringScheduler(RecommendationEngine engine,
                             NotificationManager notificationManager,
                             DiaryDao diaryDao) {
        this.engine = engine;
        this.notificationManager = notificationManager;
        this.diaryDao = diaryDao;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void runDailyCheck(List<Plant> plants) {
        executor.execute(() -> {
            Map<String, List<DiaryEntry>> diaryMap = new HashMap<>();
            for (Plant plant : plants) {
                diaryMap.put(plant.getId(), diaryDao.getEntriesForPlantSync(plant.getId()));
            }
            List<Plant> toWater = engine.getPlantsNeedingWater(plants, diaryMap);
            for (Plant plant : toWater) {
                List<DiaryEntry> entries = diaryMap.get(plant.getId());
                if (entries == null) {
                    continue;
                }
                int days = engine.daysSinceLastWatering(plant, entries);
                notificationManager.notifyWateringNeeded(plant, days);
                DiaryEntry log = new DiaryEntry(
                        java.util.UUID.randomUUID().toString(),
                        plant.getId(),
                        System.currentTimeMillis(),
                        "Watering reminder triggered",
                        "",
                        "recommendation"
                );
                diaryDao.insert(log);
            }
        });
    }

    @SuppressWarnings("unused")
    public void shutdown() {
        executor.shutdown();
    }
}