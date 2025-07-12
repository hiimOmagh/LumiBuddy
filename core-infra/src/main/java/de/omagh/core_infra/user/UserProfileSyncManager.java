package de.omagh.core_infra.user;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * Stub manager simulating user profile sync with a future cloud backend.
 */
public class UserProfileSyncManager {
    private final UserProfileManager localManager;
    private final FirebaseManager firebaseManager = new FirebaseManager();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    public UserProfileSyncManager(Context context) {
        this.localManager = new UserProfileManager(context);
    }

    /**
     * Pretend to fetch the profile from the cloud and store locally.
     */
    public void syncFromCloud() {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    String uid = firebaseManager.getUser().getUid();
                    firebaseManager.getDb().collection("profiles")
                            .document(uid)
                            .get()
                            .addOnSuccessListener(executor, doc -> {
                                if (doc != null && doc.exists()) {
                                    String name = doc.getString("name");
                                    String avatar = doc.getString("avatarUri");
                                    String theme = doc.getString("theme");
                                    if (name != null) localManager.setUsername(name);
                                    if (avatar != null) localManager.setAvatarUri(avatar);
                                    if (theme != null) localManager.setTheme(theme);
                                }
                            });
                }));
    }

    /**
     * Pretend to push the local profile to the cloud.
     */
    public void syncToCloud() {
        executor.execute(() -> firebaseManager.signInAnonymously()
                .addOnSuccessListener(executor, r -> {
                    String uid = firebaseManager.getUser().getUid();
                    Map<String, Object> data = new HashMap<>();
                    data.put("name", localManager.getUsername());
                    data.put("avatarUri", localManager.getAvatarUri());
                    data.put("theme", localManager.getTheme());
                    firebaseManager.getDb().collection("profiles")
                            .document(uid)
                            .set(data);
                }));
    }

    public UserProfileManager getLocalManager() {
        return localManager;
    }
}