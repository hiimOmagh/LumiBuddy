package de.omagh.lumibuddy.feature_recommendation;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import java.util.List;
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
            for (Plant plant : plants) {
                List<DiaryEntry> entriesForPlant = diaryDao.getEntriesForPlantSync(plant.getId());
                if (engine.shouldWater(plant, entriesForPlant)) {
                    int days = engine.daysSinceLastWatering(plant, entriesForPlant);
                    notificationManager.notifyWateringNeeded(plant.getName(), days);
                }
            }
        });
    }

    public void shutdown() {
        executor.shutdown();
    }
}