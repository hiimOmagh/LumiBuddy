package de.omagh.lumibuddy.feature_recommendation;

import java.util.List;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

/**
 * Stub scheduler that could run periodic scans and care checks.
 * For now it simply invokes the watering scheduler on a weekly basis.
 */
public class PeriodicScanScheduler {

    private final WateringScheduler wateringScheduler;

    public PeriodicScanScheduler(WateringScheduler wateringScheduler) {
        this.wateringScheduler = wateringScheduler;
    }

    public void runWeekly(List<Plant> plants) {
        wateringScheduler.runDailyCheck(plants);
    }
}