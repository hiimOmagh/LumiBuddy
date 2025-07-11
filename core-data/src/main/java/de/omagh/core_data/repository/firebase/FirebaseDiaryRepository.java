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
    private final FirestoreDiaryEntryDao dao = new FirestoreDiaryEntryDao();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        return dao.getEntriesForPlant(plantId);
    }

    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        return dao.getEntriesForPlantSync(plantId);
    }

    public void insert(DiaryEntry entry) {
        executor.execute(() -> dao.insert(entry));
    }

    public void delete(DiaryEntry entry) {
        executor.execute(() -> dao.delete(entry));
    }
}
