package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.omagh.core_data.model.DiaryEntry;

/**
 * Interface for diary data operations used by view models.
 */
public interface DiaryDataSource {
    LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId);

    List<DiaryEntry> getEntriesForPlantSync(String plantId);

    void insert(DiaryEntry entry);

    void update(DiaryEntry entry);
    void delete(DiaryEntry entry);
}