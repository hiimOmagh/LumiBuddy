package de.omagh.core_infra.sync;

import java.util.ArrayList;
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
     * Loads plant data from the cloud backend and merges it with the local
     * database.
     */
    public void loadFromCloud(MergeCallback callback) {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> firebaseManager.getDb()
                        .collection("plants")
                        .get()
                        .addOnSuccessListener(executor, snap -> {
                            List<Plant> result = new ArrayList<>();
                            for (com.google.firebase.firestore.DocumentSnapshot d : snap.getDocuments()) {
                                String id = d.getString("id");
                                String name = d.getString("name");
                                String type = d.getString("type");
                                String imageUri = d.getString("imageUri");
                                result.add(new Plant(id, name, type, imageUri));
                            }
                            callback.mergeWithLocal(result);
                        })));
    }

    /**
     * Pushes local plant data to the cloud backend.
     */
    public void syncToCloud(List<Plant> plants) {
        executor.execute(() -> {
            firebaseManager.signInAnonymously().addOnSuccessListener(r -> {
                for (Plant p : plants) {
                    firebaseManager.getDb().collection("plants")
                            .document(p.getId())
                            .set(p);
                }
            });
        });
    }

    public interface MergeCallback {
        void mergeWithLocal(List<Plant> plants);
    }

    /**
     * Resolves conflicts between local and remote plant lists.
     */
    public List<Plant> resolveConflicts(List<Plant> local, List<Plant> remote) {
        Timber.tag(TAG).d("resolveConflicts: not implemented");
        return local;
    }
}