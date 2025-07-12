package de.omagh.core_infra.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.repository.firebase.FirestorePlantDao;
import de.omagh.core_infra.firebase.FirebaseManager;
import timber.log.Timber;

/**
 * Manager responsible for synchronizing the user's plant database with a cloud
 * backend. All methods are currently stubs and should be extended when a real
 * backend service is integrated.
 */
public class PlantSyncManager {
    private static final String TAG = "PlantSyncManager";

    private final FirebaseManager firebaseManager;
    private final FirestorePlantDao cloudDao;
    private final PlantRepository localRepository;
    private final ExecutorService executor;

    @Inject
    public PlantSyncManager(PlantRepository localRepository,
                            FirestorePlantDao cloudDao,
                            FirebaseManager firebaseManager,
                            AppExecutors executors) {
        this.localRepository = localRepository;
        this.cloudDao = cloudDao;
        this.firebaseManager = firebaseManager;
        this.executor = executors.single();
    }

    /**
     * Loads plant data from the cloud backend and merges it with the local
     * database.
     */
    public void loadFromCloud(MergeCallback callback) {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    List<Plant> remote = cloudDao.getAllSync();
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
                }));
    }

    /**
     * Pushes local plant data to the cloud backend.
     */
    public void syncToCloud(List<Plant> plants) {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    for (Plant p : plants) {
                        cloudDao.insert(p);
                    }
                }));
    }

    public interface MergeCallback {
        void mergeWithLocal(List<Plant> plants);
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
        for (Plant p : remote) {
            // remote wins by replacing existing entry
            result.put(p.getId(), p);
        }
        return new ArrayList<>(result.values());
    }
}