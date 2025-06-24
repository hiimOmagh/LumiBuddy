package de.omagh.core_data.model;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.repository.DiaryRepository;

/**
 * ViewModel for managing diary entries (timeline) for a specific plant.
 */
public class DiaryViewModel extends AndroidViewModel {

    private final DiaryRepository repository;

    public DiaryViewModel(@NonNull Application application) {
        super(application);
        DiaryDao dao = AppDatabase.getInstance(application).diaryDao();
        repository = new DiaryRepository(dao);
    }

    public LiveData<List<DiaryEntry>> getDiaryEntriesForPlant(String plantId) {
        return repository.getEntriesForPlant(plantId);
    }

    public LiveData<List<DiaryEntry>> getAllEntries() {
        return repository.getAllEntries();
    }

    public List<DiaryEntry> getDiaryEntriesForPlantSync(String plantId) {
        return repository.getEntriesForPlantSync(plantId);
    }

    public List<DiaryEntry> getAllEntriesSync() {
        return repository.getAllEntriesSync();
    }

    public void addEntry(DiaryEntry entry) {
        repository.insert(entry);
    }

    public void deleteEntry(DiaryEntry entry) {
        repository.delete(entry);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
    }

    /**
     * Factory to inject DAO and plantId into ViewModel
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;

        public Factory(Application app) {
            this.application = app;
        }

        @androidx.annotation.NonNull
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