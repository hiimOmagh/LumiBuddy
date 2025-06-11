package de.omagh.lumibuddy.feature_diary;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.db.DiaryDao;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

import org.jspecify.annotations.NonNull;

/**
 * ViewModel for managing diary entries (timeline) for a specific plant.
 */
public class DiaryViewModel extends AndroidViewModel {

    private final DiaryDao diaryDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        diaryDao = AppDatabase.getInstance(application).diaryDao();
    }

    public LiveData<List<DiaryEntry>> getDiaryEntriesForPlant(String plantId) {
        return diaryDao.getEntriesForPlant(plantId);
    }

    public List<DiaryEntry> getDiaryEntriesForPlantSync(String plantId) {
        return diaryDao.getEntriesForPlantSync(plantId);
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
        private final Application application;

        public Factory(Application app) {
            this.application = app;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(DiaryViewModel.class)) {
                return (T) new DiaryViewModel(application);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
