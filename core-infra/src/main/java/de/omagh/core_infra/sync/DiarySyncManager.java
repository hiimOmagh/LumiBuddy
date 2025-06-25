package de.omagh.core_infra.sync;

import java.util.List;

import de.omagh.core_data.model.DiaryEntry;
import timber.log.Timber;

/**
 * Manager responsible for synchronizing diary entries with a future cloud
 * backend. Methods are placeholders to be filled in when remote storage is
 * implemented.
 */
public class DiarySyncManager {

    private static final String TAG = "DiarySyncManager";

    /**
     * Uploads local diary entries to the cloud backend.
     */
    public void syncToCloud() {
        Timber.tag(TAG).d("syncToCloud: not implemented");
    }

    /**
     * Downloads diary entries from the cloud backend and merges them with the
     * local database.
     */
    public void loadFromCloud() {
        Timber.tag(TAG).d("loadFromCloud: not implemented");
    }

    /**
     * Resolves conflicts between local and remote diary entry lists.
     */
    public List<DiaryEntry> resolveConflicts(List<DiaryEntry> local, List<DiaryEntry> remote) {
        Timber.tag(TAG).d("resolveConflicts: not implemented");
        return local;
    }
}
