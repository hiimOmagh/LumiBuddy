package de.omagh.feature_diary.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.sync.DiarySyncManager;
import de.omagh.core_infra.sync.SyncStatus;
import de.omagh.feature_diary.di.DaggerDiaryComponent;
import de.omagh.feature_diary.di.DiaryComponent;

/**
 * ViewModel for managing diary entries (timeline) for a specific plant.
 */
public class DiaryViewModel extends AndroidViewModel {

    @Inject
    DiaryRepository repository;
    @Inject
    DiarySyncManager syncManager;
    private LiveData<SyncStatus> syncStatus;
    private LiveData<String> syncError;

    public DiaryViewModel(@NonNull Application application) {
        this(application, null, null);
    }

    // Constructor for tests allowing repository injection
    public DiaryViewModel(@NonNull Application application, DiaryRepository repository, DiarySyncManager syncManager) {
        super(application);
        if (repository != null) {
            this.repository = repository;
        } else {
            CoreComponent core = ((CoreComponentProvider) application).getCoreComponent();
            DiaryComponent component = DaggerDiaryComponent.factory().create(core);
            component.inject(this);
        }
        if (syncManager != null) {
            this.syncManager = syncManager;
        }
        if (this.syncManager != null) {
            syncStatus = this.syncManager.getSyncStatus();
            syncError = this.syncManager.getError();
        }
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

    public void updateEntry(DiaryEntry entry) {
        repository.update(entry);
    }

    public void deleteEntry(DiaryEntry entry) {
        repository.delete(entry);
    }

    public LiveData<SyncStatus> getSyncStatus() {
        return syncStatus;
    }

    public LiveData<String> getSyncError() {
        return syncError;
    }

    public void triggerSync() {
        if (syncManager != null) syncManager.sync();
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
                return (T) new DiaryViewModel(application, null, null);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}