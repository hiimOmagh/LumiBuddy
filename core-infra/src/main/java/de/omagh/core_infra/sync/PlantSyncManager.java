package de.omagh.core_infra.sync;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.firebase.FirebaseManager;
import timber.log.Timber;

/**
 * Manager responsible for synchronizing the user's plant database with a cloud
 * backend. All methods are currently stubs and should be extended when a real
 * backend service is integrated.
 */
public class PlantSyncManager {
    private static final String TAG = "PlantSyncManager";
    private final FirebaseManager firebaseManager = new FirebaseManager();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Pushes local plant data to the cloud backend.
     */
    public void syncToCloud(List<Plant> plants) {
        executor.execute(() -> {
            firebaseManager.signInAnonymously().addOnSuccessListener(r -> {
                for (Plant p : plants) {
                    firebaseManager.getDb().collection("plants")
                            .document(p.id)
                            .set(p);
                }
            });
        });
    }

    /**
     * Loads plant data from the cloud backend and merges it with the local
     * database.
     */
    public void loadFromCloud() {
        Timber.tag(TAG).d("loadFromCloud: not implemented");
    }

    /**
     * Resolves conflicts between local and remote plant lists.
     */
    public List<Plant> resolveConflicts(List<Plant> local, List<Plant> remote) {
        Timber.tag(TAG).d("resolveConflicts: not implemented");
        return local;
    }
}