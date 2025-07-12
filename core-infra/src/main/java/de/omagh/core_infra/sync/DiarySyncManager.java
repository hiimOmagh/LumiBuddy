package de.omagh.core_infra.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.firebase.FirestoreDiaryEntryDao;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.firebase.FirebaseManager;
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

    @Inject
    public DiarySyncManager(DiaryRepository localRepository,
                            FirestoreDiaryEntryDao cloudDao,
                            FirebaseManager firebaseManager,
                            AppExecutors executors) {
        this.localRepository = localRepository;
        this.cloudDao = cloudDao;
        this.firebaseManager = firebaseManager;
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
