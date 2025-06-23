package de.omagh.lumibuddy.feature_diary;

import timber.log.Timber;

import java.util.List;

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
     *
     * @param local  entries stored locally
     * @param remote entries fetched from the cloud
     * @return the list that should be kept after conflict resolution
     */
    public List<DiaryEntry> resolveConflicts(List<DiaryEntry> local, List<DiaryEntry> remote) {
        Timber.tag(TAG).d("resolveConflicts: not implemented");
        return local;
    }
}
