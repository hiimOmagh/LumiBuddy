package de.omagh.lumibuddy.feature_diary;

import android.util.Log;

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
        Log.d(TAG, "syncToCloud: not implemented");
    }

    /**
     * Downloads diary entries from the cloud backend and merges them with the
     * local database.
     */
    public void loadFromCloud() {
        Log.d(TAG, "loadFromCloud: not implemented");
    }

    /**
     * Resolves conflicts between local and remote diary entry lists.
     *
     * @param local  entries stored locally
     * @param remote entries fetched from the cloud
     * @return the list that should be kept after conflict resolution
     */
    public List<DiaryEntry> resolveConflicts(List<DiaryEntry> local, List<DiaryEntry> remote) {
        Log.d(TAG, "resolveConflicts: not implemented");
        return local;
    }
}
