package de.omagh.core_infra.recommendation;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.DiaryEntry;

public class RecommendationEngineTest {
    @Test
    public void daysSinceLastWatering_returnsMaxWhenNoEntries() {
        Plant plant = new Plant("1", "Basil", "Basil", "");
        RecommendationEngine engine = new RecommendationEngine();
        int days = engine.daysSinceLastWatering(plant, Collections.emptyList());
        assertEquals(Integer.MAX_VALUE, days);
    }

    @Test
    public void shouldWater_trueWhenIntervalExceeded() {
        Plant plant = new Plant("1", "Tomato", "Tomato", "");
        long fourDaysAgo = System.currentTimeMillis() - 4L * 24 * 60 * 60 * 1000;
        DiaryEntry entry = new DiaryEntry(
                UUID.randomUUID().toString(),
                plant.getId(),
                fourDaysAgo,
                "",
                "",
                "watering"
        );
        RecommendationEngine engine = new RecommendationEngine();
        boolean result = engine.shouldWater(plant, java.util.Collections.singletonList(entry));
        assertTrue(result);
    }
}
