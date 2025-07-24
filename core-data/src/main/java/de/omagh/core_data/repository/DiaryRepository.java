package de.omagh.core_data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import de.omagh.core_domain.util.AppExecutors;

import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.model.DiaryEntry;

/**
 * Repository providing an abstraction over {@link DiaryDao}.
 */
public class DiaryRepository implements DiaryDataSource {
    private final DiaryDao diaryDao;
    private final ExecutorService executor;
    private final de.omagh.core_domain.sync.SyncScheduler scheduler;

    public DiaryRepository(Context context,
                           DiaryDao dao,
                           AppExecutors executors,
                           de.omagh.core_domain.sync.SyncScheduler scheduler) {
        this.diaryDao = dao;
        this.executor = executors.single();
        this.scheduler = scheduler;
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
        scheduler.scheduleDaily();
    }

    public void update(DiaryEntry entry) {
        executor.execute(() -> diaryDao.update(entry));
        scheduler.scheduleDaily();
    }

    public void delete(DiaryEntry entry) {
        executor.execute(() -> diaryDao.delete(entry));
        scheduler.scheduleDaily();
    }

    public void shutdown() {
        executor.shutdown();
    }
}