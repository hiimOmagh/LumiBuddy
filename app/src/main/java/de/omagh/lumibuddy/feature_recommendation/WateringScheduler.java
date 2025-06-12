package de.omagh.lumibuddy.feature_recommendation;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.data.db.DiaryDao;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

/**
 * Performs a daily check for each plant and triggers notifications if
 * watering is due.
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

    /**
     * Iterates over the given plants and diary entries and sends a notification
     * if any plant is overdue for watering.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void runDailyCheck(List<Plant> plants) {
        executor.execute(() -> {
            Map<String, List<DiaryEntry>> diaryMap = new HashMap<>();
            for (Plant plant : plants) {
                diaryMap.put(plant.getId(), diaryDao.getEntriesForPlantSync(plant.getId()));
            }
            List<Plant> toWater = engine.getPlantsNeedingWater(plants, diaryMap);
            for (Plant plant : toWater) {
                int days = engine.daysSinceLastWatering(plant, diaryMap.get(plant.getId()));
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

    public void shutdown() {
        executor.shutdown();
    }
}