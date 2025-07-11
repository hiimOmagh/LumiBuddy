package de.omagh.core_infra.recommendation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_domain.model.PlantCareProfile;
import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.plantdb.PlantInfo;
import de.omagh.core_domain.model.PlantStage;
import de.omagh.core_domain.util.MeasurementUtils;

/**
 * Simple recommendation engine used to provide plant care suggestions
 * based on diary history and care profiles.
 */
public class RecommendationEngine {

    private final PlantDatabaseManager db = new PlantDatabaseManager();
    private final java.util.concurrent.ExecutorService executor =
            java.util.concurrent.Executors.newSingleThreadExecutor();

    /**
     * Determines if the given plant should be watered based on its diary entries and care profile.
     */
    public boolean shouldWater(Plant plant, List<DiaryEntry> entries) {
        PlantInfo info = db.getPlantByName(plant.getType());
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
            return true;
        }

        long daysSince = (System.currentTimeMillis() - lastWater) /
                (24L * 60L * 60L * 1000L);
        return daysSince >= interval;
    }

    /**
     * Returns the number of whole days since the last watering event.
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
     * Checks all given plants and returns those that require watering.
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

    /**
     * Generates a light-related recommendation for the plant if recent measurements fall outside the ideal DLI range.
     */
    public String getLightRecommendation(Plant plant, List<DiaryEntry> entries) {
        if (plant == null || entries == null) return null;

        PlantInfo info = db.getPlantByName(plant.getType());
        if (info == null) return null;
        PlantCareProfile profile = info.getProfileForStage(PlantStage.VEGETATIVE);
        if (profile == null) return null;

        float minDli = profile.getMinDLI();
        float maxDli = profile.getMaxDLI();

        DiaryEntry latestLight = null;
        float latestDli = Float.NaN;
        float latestPpfd = Float.NaN;
        for (DiaryEntry e : entries) {
            if ("light".equalsIgnoreCase(e.getEventType())) {
                float[] vals = parseLightMetrics(e.getNote());
                if (latestLight == null || e.getTimestamp() > latestLight.getTimestamp()) {
                    latestLight = e;
                    latestDli = vals[0];
                    latestPpfd = vals[1];
                }
            }
        }

        if (latestLight == null) {
            return null;
        }

        if (Float.isNaN(latestDli) && !Float.isNaN(latestPpfd)) {
            latestDli = MeasurementUtils.ppfdToDLI(latestPpfd, 24);
        }

        if (Float.isNaN(latestDli)) {
            return null;
        }

        long now = System.currentTimeMillis();
        long recent = now - 3L * 24L * 60L * 60L * 1000L;
        for (DiaryEntry e : entries) {
            if ("recommendation".equalsIgnoreCase(e.getEventType())
                    && e.getTimestamp() >= recent) {
                String note = e.getNote() != null ? e.getNote().toLowerCase() : "";
                if (note.contains("light")) {
                    return null;
                }
            }
        }

        if (latestDli < minDli) {
            return "Light level low - move to brighter spot or increase light duration.";
        } else if (latestDli > maxDli) {
            return "Light level high - reduce exposure or consider diffusing light.";
        }
        return null;
    }

    float computeAverageDli(List<DiaryEntry> lightEntries) {
        if (lightEntries == null || lightEntries.isEmpty()) return Float.NaN;
        float sum = 0f;
        int count = 0;
        for (DiaryEntry e : lightEntries) {
            float[] vals = parseLightMetrics(e.getNote());
            float dli = vals[0];
            if (Float.isNaN(dli) && !Float.isNaN(vals[1])) {
                dli = MeasurementUtils.ppfdToDLI(vals[1], 24);
            }
            if (!Float.isNaN(dli)) {
                sum += dli;
                count++;
            }
        }
        return count > 0 ? sum / count : Float.NaN;
    }

    public void checkLightRecommendations(
            List<Plant> plants,
            Function<String, List<DiaryEntry>> entryFetcher,
            Consumer<DiaryEntry> entryInserter) {
        if (plants == null || entryFetcher == null || entryInserter == null) return;

        executor.execute(() ->
                performLightCheck(plants, entryFetcher, entryInserter));
    }

    private void performLightCheck(List<Plant> plants,
                                   Function<String, List<DiaryEntry>> entryFetcher,
                                   Consumer<DiaryEntry> entryInserter) {

        long now = System.currentTimeMillis();
        long cutoff = now - 3L * 24L * 60L * 60L * 1000L;

        for (Plant plant : plants) {
            List<DiaryEntry> entries = entryFetcher.apply(plant.getId());
            if (entries == null) continue;

            PlantInfo info = db.getPlantByName(plant.getType());
            if (info == null) continue;
            PlantCareProfile profile = info.getProfileForStage(PlantStage.VEGETATIVE);
            if (profile == null) continue;

            boolean hasRecent = false;
            List<DiaryEntry> lightEntries = new ArrayList<>();
            for (DiaryEntry e : entries) {
                if (e.getTimestamp() < cutoff) continue;
                if ("recommendation".equalsIgnoreCase(e.getEventType())) {
                    String note = e.getNote() != null ? e.getNote().toLowerCase() : "";
                    if (note.contains("light low") || note.contains("light high")) {
                        hasRecent = true;
                    }
                } else if ("light".equalsIgnoreCase(e.getEventType())) {
                    lightEntries.add(e);
                }
            }

            if (hasRecent || lightEntries.isEmpty()) continue;

            float avg = computeAverageDli(lightEntries);
            if (Float.isNaN(avg)) continue;

            String note = null;
            if (avg < profile.getMinDLI()) {
                note = "Light low: consider moving plant to a brighter spot or increasing light duration.";
            } else if (avg > profile.getMaxDLI()) {
                note = "Light high: consider reducing exposure or diffusing light.";
            }

            if (note != null) {
                DiaryEntry rec = new DiaryEntry(
                        UUID.randomUUID().toString(),
                        plant.getId(),
                        now,
                        note,
                        "",
                        "recommendation"
                );
                entryInserter.accept(rec);
            }
        }
    }

    private float[] parseLightMetrics(String note) {
        float dli = Float.NaN;
        float ppfd = Float.NaN;
        if (note == null) return new float[]{dli, ppfd};

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("(?i)dli[:=\\s]*([0-9]+(?:\\.[0-9]+)?)")
                .matcher(note);
        if (m.find()) {
            try {
                dli = Float.parseFloat(Objects.requireNonNull(m.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }

        m = java.util.regex.Pattern
                .compile("(?i)ppfd[:=\\s]*([0-9]+(?:\\.[0-9]+)?)")
                .matcher(note);
        if (m.find()) {
            try {
                ppfd = Float.parseFloat(Objects.requireNonNull(m.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }

        if (Float.isNaN(dli)) {
            m = java.util.regex.Pattern
                    .compile("([0-9]+(?:\\.[0-9]+)?)")
                    .matcher(note);
            if (m.find()) {
                try {
                    dli = Float.parseFloat(Objects.requireNonNull(m.group(1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return new float[]{dli, ppfd};
    }

    @SuppressWarnings("unused")
    public void shutdown() {
        executor.shutdown();
    }
}
