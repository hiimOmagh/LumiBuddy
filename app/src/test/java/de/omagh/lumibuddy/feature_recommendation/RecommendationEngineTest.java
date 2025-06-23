package de.omagh.lumibuddy.feature_recommendation;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.*;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.DiaryEntry;

public class RecommendationEngineTest {

    @Test
    public void testAverageDliCalculation() {
        RecommendationEngine engine = new RecommendationEngine();
        long now = System.currentTimeMillis();
        List<DiaryEntry> entries = Arrays.asList(
                new DiaryEntry("1", "p1", now - 1000, "DLI:10", "", "light"),
                new DiaryEntry("2", "p1", now - 2000, "DLI:20", "", "light"),
                new DiaryEntry("3", "p1", now - 3000, "PPFD:300", "", "light")
        );
        float expected = (10f + 20f +
                de.omagh.lumibuddy.feature_measurement.MeasurementUtils.ppfdToDLI(300f, 24)) / 3f;
        float avg = engine.computeAverageDli(entries);
        assertEquals(expected, avg, 0.01f);
    }

    @Test
    public void testRecommendationCreationLowLight() {
        RecommendationEngine engine = new RecommendationEngine();
        long now = System.currentTimeMillis();
        Plant plant = new Plant("1", "Basil", "Basil", "");
        Map<String, List<DiaryEntry>> diaryMap = new HashMap<>();
        diaryMap.put("1", new ArrayList<>(Arrays.asList(
                new DiaryEntry("a", "1", now - 1000, "DLI:5", "", "light"),
                new DiaryEntry("b", "1", now - 2000, "DLI:5", "", "light"),
                new DiaryEntry("c", "1", now - 2500, "DLI:5", "", "light")
        )));
        List<DiaryEntry> inserted = new ArrayList<>();

        engine.checkLightRecommendations(
                Collections.singletonList(plant),
                id -> diaryMap.get(id),
                e -> inserted.add(e)
        );

        assertEquals(1, inserted.size());
        assertTrue(inserted.get(0).getNote().startsWith("Light low"));
    }
}
