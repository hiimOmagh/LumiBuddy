package de.omagh.lumibuddy.feature_diary;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.DiaryDao;

/**
 * Repository providing an abstraction over {@link DiaryDao}.
 */
public class DiaryRepository implements DiaryDataSource {
    private final DiaryDao diaryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DiaryRepository(DiaryDao dao) {
        this.diaryDao = dao;
    }

    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        return diaryDao.getEntriesForPlant(plantId);
    }

    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        return diaryDao.getEntriesForPlantSync(plantId);
    }

    public LiveData<List<DiaryEntry>> getAllEntries() {
        return diaryDao.getAllEntries();
    }

    public List<DiaryEntry> getAllEntriesSync() {
        return diaryDao.getAllEntriesSync();
    }

    public void insert(DiaryEntry entry) {
        executor.execute(() -> diaryDao.insert(entry));
    }

    public void delete(DiaryEntry entry) {
        executor.execute(() -> diaryDao.delete(entry));
    }

    public void shutdown() {
        executor.shutdown();
    }
}