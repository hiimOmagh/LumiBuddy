package de.omagh.lumibuddy.feature_recommendation;

import java.util.ArrayList;
import java.util.List;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

/**
 * Performs a daily check for each plant and triggers notifications if
 * watering is due.
 */
public class WateringScheduler {

    private final RecommendationEngine engine;
    private final NotificationManager notificationManager;

    public WateringScheduler(RecommendationEngine engine,
                             NotificationManager notificationManager) {
        this.engine = engine;
        this.notificationManager = notificationManager;
    }

    /**
     * Iterates over the given plants and diary entries and sends a notification
     * if any plant is overdue for watering.
     */
    public void runDailyCheck(List<Plant> plants, List<DiaryEntry> allEntries) {
        for (Plant plant : plants) {
            List<DiaryEntry> entriesForPlant = new ArrayList<>();
            for (DiaryEntry entry : allEntries) {
                if (plant.getId().equals(entry.getPlantId())) {
                    entriesForPlant.add(entry);
                }
            }
            if (engine.shouldWater(plant, entriesForPlant)) {
                int days = engine.daysSinceLastWatering(plant, entriesForPlant);
                notificationManager.notifyWateringNeeded(plant.getName(), days);
            }
        }
    }
}