package de.omagh.lumibuddy.feature_plantdb;

import android.util.Log;

import java.util.List;

import de.omagh.core_domain.model.Plant;

/**
 * Manager responsible for synchronizing the user's plant database with a cloud
 * backend. All methods are currently stubs and should be extended when a real
 * backend service is integrated.
 */
public class PlantSyncManager {
    private static final String TAG = "PlantSyncManager";

    /**
     * Pushes local plant data to the cloud backend.
     * <p>
     * Future implementations should send the locally stored plants to a
     * remote server for backup and multi-device synchronization.
     * </p>
     */
    public void syncToCloud() {
        Timber.tag(TAG).d("syncToCloud: not implemented");
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
     *
     * @param local  plants stored on the device
     * @param remote plants retrieved from the cloud
     * @return the list that should be kept after conflict resolution
     */
    public List<Plant> resolveConflicts(List<Plant> local, List<Plant> remote) {
        Timber.tag(TAG).d("resolveConflicts: not implemented");
        return local;
    }
}