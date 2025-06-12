package de.omagh.lumibuddy.feature_recommendation;

import android.Manifest;

import androidx.annotation.RequiresPermission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.DiaryDao;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

/**
 * Performs periodic checks for light level recommendations.
 */
public class LightRecommendationScheduler {

    private final RecommendationEngine engine;
    private final NotificationManager notificationManager;
    private final DiaryDao diaryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LightRecommendationScheduler(RecommendationEngine engine,
                                        NotificationManager notificationManager,
                                        DiaryDao diaryDao) {
        this.engine = engine;
        this.notificationManager = notificationManager;
        this.diaryDao = diaryDao;
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void runLightCheck(List<Plant> plants) {
        executor.execute(() -> {
            Map<String, List<DiaryEntry>> diaryMap = new HashMap<>();
            for (Plant plant : plants) {
                diaryMap.put(plant.getId(), diaryDao.getEntriesForPlantSync(plant.getId()));
            }
            for (Plant plant : plants) {
                List<DiaryEntry> entries = diaryMap.get(plant.getId());
                if (entries == null) continue;
                String note = engine.getLightRecommendation(plant, entries);
                if (note != null) {
                    notificationManager.notifyLightRecommendation(plant, note);
                    DiaryEntry log = new DiaryEntry(
                            java.util.UUID.randomUUID().toString(),
                            plant.getId(),
                            System.currentTimeMillis(),
                            note,
                            "",
                            "recommendation"
                    );
                    diaryDao.insert(log);
                }
            }
        });
    }

    @SuppressWarnings("unused")
    public void shutdown() {
        executor.shutdown();
    }
}