package de.omagh.core_infra.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.firebase.FirestoreDiaryEntryDao;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.sync.SyncStatus;
import timber.log.Timber;

/**
 * Manager responsible for synchronizing diary entries with a future cloud
 * backend. Methods are placeholders to be filled in when remote storage is
 * implemented.
 */
public class DiarySyncManager {

    private static final String TAG = "DiarySyncManager";

    private final FirebaseManager firebaseManager;
    private final FirestoreDiaryEntryDao cloudDao;
    private final DiaryRepository localRepository;
    private final ExecutorService executor;
    private final SettingsManager settings;

    private final MutableLiveData<SyncStatus> status = new MutableLiveData<>(SyncStatus.IDLE);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public DiarySyncManager(DiaryRepository localRepository,
                            FirestoreDiaryEntryDao cloudDao,
                            FirebaseManager firebaseManager,
                            SettingsManager settingsManager,
                            AppExecutors executors) {
        this.localRepository = localRepository;
        this.cloudDao = cloudDao;
        this.firebaseManager = firebaseManager;
        this.settings = settingsManager;
        this.executor = executors.single();
    }

    /**
     * Uploads local diary entries to the cloud backend.
     */
    public void syncToCloud() {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    List<DiaryEntry> local = localRepository.getAllEntriesSync();
                    for (DiaryEntry e : local) {
                        cloudDao.insert(e);
                    }
                }));
    }

    /**
     * Downloads diary entries from the cloud backend and merges them with the
     * local database.
     */
    public void loadFromCloud() {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    List<DiaryEntry> local = localRepository.getAllEntriesSync();
                    Set<String> plantIds = new HashSet<>();
                    for (DiaryEntry e : local) {
                        plantIds.add(e.getPlantId());
                    }
                    List<DiaryEntry> remote = new ArrayList<>();
                    for (String id : plantIds) {
                        remote.addAll(cloudDao.getEntriesForPlantSync(id));
                    }

                    List<DiaryEntry> merged = resolveConflicts(local, remote);

                    Map<String, DiaryEntry> localMap = new HashMap<>();
                    for (DiaryEntry e : local) {
                        localMap.put(e.getId(), e);
                    }

                    for (DiaryEntry e : remote) {
                        DiaryEntry le = localMap.get(e.getId());
                        if (le == null) {
                            localRepository.insert(e);
                        } else if (e.getTimestamp() > le.getTimestamp()) {
                            localRepository.delete(le);
                            localRepository.insert(e);
                        }
                    }
                }));
    }

    public LiveData<SyncStatus> getSyncStatus() {
        return status;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Performs bidirectional sync.
     */
    public void sync() {
        status.postValue(SyncStatus.SYNCING);
        executor.execute(() -> {
            try {
                com.google.android.gms.tasks.Tasks.await(firebaseManager.signInAnonymously());
                List<DiaryEntry> local = localRepository.getAllEntriesSync();
                Set<String> plantIds = new HashSet<>();
                for (DiaryEntry e : local) {
                    plantIds.add(e.getPlantId());
                }
                List<DiaryEntry> remote = new ArrayList<>();
                for (String id : plantIds) {
                    remote.addAll(cloudDao.getEntriesForPlantSync(id));
                }
                List<DiaryEntry> merged = resolveConflicts(local, remote);
                Map<String, DiaryEntry> localMap = new HashMap<>();
                for (DiaryEntry e : local) {
                    localMap.put(e.getId(), e);
                }
                for (DiaryEntry e : remote) {
                    DiaryEntry le = localMap.get(e.getId());
                    if (le == null) {
                        localRepository.insert(e);
                    } else if (e.getTimestamp() > le.getTimestamp()) {
                        localRepository.delete(le);
                        localRepository.insert(e);
                    }
                }
                for (DiaryEntry e : merged) {
                    cloudDao.insert(e);
                }
                settings.setDiaryLastSync(System.currentTimeMillis());
                status.postValue(SyncStatus.SUCCESS);
            } catch (Exception ex) {
                Timber.e(ex);
                error.postValue(ex.getMessage());
                status.postValue(SyncStatus.ERROR);
            }
        });
    }

    /**
     * Resolves conflicts between local and remote diary entry lists.
     */
    public List<DiaryEntry> resolveConflicts(List<DiaryEntry> local, List<DiaryEntry> remote) {
        Map<String, DiaryEntry> result = new HashMap<>();
        if (local != null) {
            for (DiaryEntry e : local) {
                result.put(e.getId(), e);
            }
        }
        for (DiaryEntry e : remote) {
            DiaryEntry current = result.get(e.getId());
            if (current == null || e.getTimestamp() > current.getTimestamp()) {
                result.put(e.getId(), e);
            }
        }
        return new ArrayList<>(result.values());
    }
}
