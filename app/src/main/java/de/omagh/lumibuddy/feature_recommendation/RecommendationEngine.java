package de.omagh.lumibuddy.feature_recommendation;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;
import de.omagh.lumibuddy.feature_plantdb.PlantCareProfile;
import de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager;
import de.omagh.lumibuddy.feature_plantdb.PlantIdentifier;
import de.omagh.lumibuddy.feature_plantdb.PlantInfo;
import de.omagh.lumibuddy.feature_plantdb.PlantStage;

/**
 * Simple recommendation engine used to provide plant care suggestions
 * based on diary history and care profiles.
 */
public class RecommendationEngine {

    private final PlantDatabaseManager db = new PlantDatabaseManager();
    private final PlantIdentifier identifier = new PlantIdentifier(db);

    /**
     * Determines if the given plant should be watered based on its diary
     * entries and care profile.
     *
     * @param plant   The plant to check.
     * @param entries Diary entries for the plant (any order).
     * @return true if watering is due, false otherwise.
     */
    public boolean shouldWater(Plant plant, List<DiaryEntry> entries) {
        PlantInfo info = identifier.identifyByName(plant.getType());
        PlantCareProfile profile = null;
        if (info != null) {
            profile = info.getProfileForStage(PlantStage.VEGETATIVE);
        }
        int interval = profile != null ? profile.getWateringIntervalDays() : 3;

        long lastWater = 0;
        for (DiaryEntry entry : entries) {
            if (plant.getId().equals(entry.getPlantId()) &&
                    "watering".equalsIgnoreCase(entry.getEventType())) {
                if (entry.getTimestamp() > lastWater) {
                    lastWater = entry.getTimestamp();
                }
            }
        }

        if (lastWater == 0) {
            // No watering history - recommend watering
            return true;
        }

        long daysSince = (System.currentTimeMillis() - lastWater) /
                (24L * 60L * 60L * 1000L);
        return daysSince >= interval;
    }

    /**
     * Returns the number of whole days since the last watering event in the
     * given diary entries. If the plant was never watered, this returns
     * Integer.MAX_VALUE.
     */
    public int daysSinceLastWatering(Plant plant, List<DiaryEntry> entries) {
        long lastWater = 0;
        for (DiaryEntry entry : entries) {
            if (plant.getId().equals(entry.getPlantId()) &&
                    "watering".equalsIgnoreCase(entry.getEventType())) {
                if (entry.getTimestamp() > lastWater) {
                    lastWater = entry.getTimestamp();
                }
            }
        }
        if (lastWater == 0) {
            return Integer.MAX_VALUE;
        }
        return (int) ((System.currentTimeMillis() - lastWater) /
                (24L * 60L * 60L * 1000L));
    }

    /**
     * Checks all given plants and returns those that require watering based on
     * diary history and their care profile settings.
     *
     * @param plants   Plants to check
     * @param diaryMap Map of plantId -> diary entries
     * @return list of plants that should be watered
     */
    public List<Plant> getPlantsNeedingWater(List<Plant> plants,
                                             Map<String, List<DiaryEntry>> diaryMap) {
        List<Plant> needs = new ArrayList<>();
        if (plants == null) return needs;
        for (Plant p : plants) {
            List<DiaryEntry> entries = diaryMap != null ? diaryMap.get(p.getId()) : null;
            if (entries == null) entries = java.util.Collections.emptyList();
            if (shouldWater(p, entries)) {
                needs.add(p);
            }
        }
        return needs;
    }
}