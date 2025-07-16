package de.omagh.lumibuddy.feature_diary;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;

/**
 * Provides a small abstraction for creating and retrieving {@link DiaryEntry}
 * objects. Having a dedicated manager keeps fragments and view models light and
 * allows reuse across the app.
 */
@SuppressWarnings("unused")
public class PlantLogManager {

    private final DiaryRepository repository;

    public PlantLogManager(DiaryRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns a stream of diary entries for the given plant.
     */
    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        return repository.getEntriesForPlant(plantId);
    }

    /**
     * Returns a stream of all diary entries.
     */
    public LiveData<List<DiaryEntry>> getAllEntries() {
        return repository.getAllEntries();
    }

    /**
     * Synchronously fetches diary entries for one plant.
     */
    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        return repository.getEntriesForPlantSync(plantId);
    }

    /**
     * Synchronously fetches all diary entries.
     */
    public List<DiaryEntry> getAllEntriesSync() {
        return repository.getAllEntriesSync();
    }

    /**
     * Adds a new diary entry.
     */
    public void addEntry(DiaryEntry entry) {
        repository.insert(entry);
    }

    /**
     * Updates an existing diary entry.
     */
    public void updateEntry(DiaryEntry entry) {
        repository.update(entry);
    }

    /**
     * Deletes the given diary entry.
     */
    public void deleteEntry(DiaryEntry entry) {
        repository.delete(entry);
    }

    /**
     * Cleans up the underlying repository if needed.
     */
    public void shutdown() {
        repository.shutdown();
    }
}
