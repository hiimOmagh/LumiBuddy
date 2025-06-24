package de.omagh.core_infra.user;

import android.content.Context;

/**
 * Stub manager simulating user profile sync with a future cloud backend.
 */
public class UserProfileSyncManager {
    private final UserProfileManager localManager;

    public UserProfileSyncManager(Context context) {
        this.localManager = new UserProfileManager(context);
    }

    /**
     * Pretend to fetch the profile from the cloud and store locally.
     */
    public void syncFromCloud() {
        // Placeholder for future REST/Firebase implementation
    }

    /**
     * Pretend to push the local profile to the cloud.
     */
    public void syncToCloud() {
        // Placeholder for future REST/Firebase implementation
    }

    public UserProfileManager getLocalManager() {
        return localManager;
    }
}