package de.omagh.lumibuddy.feature_diary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.DiaryDao;

/**
 * ViewModel for managing diary entries (timeline) for a specific plant.
 */
public class DiaryViewModel extends ViewModel {

    private final DiaryDao diaryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DiaryViewModel(DiaryDao dao) {
        this.diaryDao = dao;
    }

    public LiveData<List<DiaryEntry>> getDiaryEntriesForPlant(String plantId) {
        return diaryDao.getEntriesForPlant(plantId);
    }

    public void addEntry(DiaryEntry entry) {
        executor.execute(() -> diaryDao.insert(entry));
    }

    public void deleteEntry(DiaryEntry entry) {
        executor.execute(() -> diaryDao.delete(entry));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }

    /**
     * Factory to inject DAO and plantId into ViewModel
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final DiaryDao dao;

        public Factory(DiaryDao dao) {
            this.dao = dao;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DiaryViewModel.class)) {
                return (T) new DiaryViewModel(dao);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
