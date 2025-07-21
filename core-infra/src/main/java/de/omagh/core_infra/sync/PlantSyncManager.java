package de.omagh.core_infra.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_data.repository.firebase.FirestorePlantDao;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.sync.SyncStatus;
import timber.log.Timber;

/**
 * Manager responsible for synchronizing the user's plant database with a cloud
 * backend. All methods are currently stubs and should be extended when a real
 * backend service is integrated.
 */
public class PlantSyncManager {
    private static final String TAG = "PlantSyncManager";

    private final FirebaseManager firebaseManager;
    private final PlantRepository localRepository;
    private final ExecutorService executor;
    private final SettingsManager settings;

    private final MutableLiveData<SyncStatus> status = new MutableLiveData<>(SyncStatus.IDLE);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    @Inject
    public PlantSyncManager(PlantRepository localRepository,
                            FirebaseManager firebaseManager,
                            SettingsManager settingsManager,
                            AppExecutors executors) {
        this.localRepository = localRepository;
        this.firebaseManager = firebaseManager;
        this.settings = settingsManager;
        this.executor = executors.single();
    }

    /**
     * Loads plant data from the cloud backend and merges it with the local
     * database.
     */
    public void loadFromCloud(MergeCallback callback) {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    FirestorePlantDao dao = new FirestorePlantDao(firebaseManager.getUser().getUid());
                    List<Plant> remote = dao.getAllSync();
                    List<Plant> local = localRepository.getAllPlantsSync();
                    List<Plant> merged = resolveConflicts(local, remote);

                    Map<String, Plant> localMap = new HashMap<>();
                    if (local != null) {
                        for (Plant p : local) {
                            localMap.put(p.getId(), p);
                        }
                    }
                    for (Plant p : remote) {
                        Plant lp = localMap.get(p.getId());
                        if (lp == null) {
                            localRepository.insertPlant(p);
                        } else if (!lp.getName().equals(p.getName()) ||
                                !lp.getType().equals(p.getType()) ||
                                (lp.getImageUri() != null && !lp.getImageUri().equals(p.getImageUri()))) {
                            localRepository.updatePlant(p);
                        }
                    }
                    if (callback != null) {
                        callback.mergeWithLocal(merged);
                    }
                    settings.setPlantLastSync(System.currentTimeMillis());
                }));
    }

    /**
     * Pushes local plant data to the cloud backend.
     */
    public void syncToCloud(List<Plant> plants) {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    FirestorePlantDao dao = new FirestorePlantDao(firebaseManager.getUser().getUid());
                    for (Plant p : plants) {
                        dao.insert(p);
                    }
                    settings.setPlantLastSync(System.currentTimeMillis());
                }));
    }

    public LiveData<SyncStatus> getSyncStatus() {
        return status;
    }

    public LiveData<String> getError() {
        return error;
    }

    /**
     * Triggers a full bidirectional sync.
     */
    public void sync() {
        status.postValue(SyncStatus.SYNCING);
        executor.execute(() -> {
            try {
                com.google.android.gms.tasks.Tasks.await(firebaseManager.signInAnonymously());
                FirestorePlantDao dao = new FirestorePlantDao(firebaseManager.getUser().getUid());
                List<Plant> remote = dao.getAllSync();
                List<Plant> local = localRepository.getAllPlantsSync();
                List<Plant> merged = resolveConflicts(local, remote);

                Map<String, Plant> localMap = new HashMap<>();
                if (local != null) {
                    for (Plant p : local) {
                        localMap.put(p.getId(), p);
                    }
                }
                for (Plant p : merged) {
                    Plant lp = localMap.get(p.getId());
                    if (lp == null) {
                        localRepository.insertPlant(p);
                    } else if (p.getUpdatedAt() > lp.getUpdatedAt() ||
                            !lp.getName().equals(p.getName()) ||
                            !lp.getType().equals(p.getType()) ||
                            (lp.getImageUri() != null && !lp.getImageUri().equals(p.getImageUri()))) {
                        localRepository.updatePlant(p);
                    }
                    dao.insert(p);
                }
                settings.setPlantLastSync(System.currentTimeMillis());
                status.postValue(SyncStatus.SUCCESS);
            } catch (Exception e) {
                Timber.e(e);
                error.postValue(e.getMessage());
                status.postValue(SyncStatus.ERROR);
            }
        });
    }

    /**
     * Resolves conflicts between local and remote plant lists.
     */
    public List<Plant> resolveConflicts(List<Plant> local, List<Plant> remote) {
        Map<String, Plant> result = new HashMap<>();
        if (local != null) {
            for (Plant p : local) {
                result.put(p.getId(), p);
            }
        }
        if (remote != null) {
            for (Plant p : remote) {
                Plant current = result.get(p.getId());
                if (current == null || p.getUpdatedAt() > current.getUpdatedAt()) {
                    result.put(p.getId(), p);
                }
            }
        }
        return new ArrayList<>(result.values());
    }

    public interface MergeCallback {
        void mergeWithLocal(List<Plant> plants);
    }
}
