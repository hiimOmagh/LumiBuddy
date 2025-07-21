package de.omagh.core_data.repository.firebase;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryDataSource;

/**
 * Firestore-backed implementation of {@link DiaryDataSource}.
 */
public class FirebaseDiaryRepository implements DiaryDataSource {
    private final FirestoreDiaryEntryDao dao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FirebaseDiaryRepository(String uid) {
        this.dao = new FirestoreDiaryEntryDao(uid);
    }

    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        return dao.getEntriesForPlant(plantId);
    }

    public LiveData<List<DiaryEntry>> getAllEntries() {
        return dao.getAll();
    }

    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        return dao.getEntriesForPlantSync(plantId);
    }

    public List<DiaryEntry> getAllEntriesSync() {
        return dao.getAllSync();
    }

    public void insert(DiaryEntry entry) {
        executor.execute(() -> dao.insert(entry));
    }

    public void update(DiaryEntry entry) {
        executor.execute(() -> dao.update(entry));
    }

    public void delete(DiaryEntry entry) {
        executor.execute(() -> dao.delete(entry));
    }
}
