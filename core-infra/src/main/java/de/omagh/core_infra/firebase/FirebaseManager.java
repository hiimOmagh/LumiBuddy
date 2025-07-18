package de.omagh.core_infra.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

/**
 * Lightweight helper exposing FirebaseAuth and Firestore access.
 */
public class FirebaseManager {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<FirebaseUser> userLiveData = new MutableLiveData<>();
    private FirebaseUser cachedUser;

    public FirebaseManager() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public Task<com.google.firebase.auth.AuthResult> signInAnonymously() {
        return auth.signInAnonymously();
    }

    /**
     * Signs in anonymously and returns a LiveData of the authenticated user.
     * The user is cached so subsequent calls return immediately.
     */
    public LiveData<FirebaseUser> signIn() {
        if (cachedUser != null) {
            userLiveData.postValue(cachedUser);
            return userLiveData;
        }
        auth.signInAnonymously()
                .addOnSuccessListener(result -> {
                    cachedUser = result.getUser();
                    userLiveData.postValue(cachedUser);
                })
                .addOnFailureListener(e -> userLiveData.postValue(null));
        return userLiveData;
    }

    public FirebaseUser getUser() {
        return cachedUser != null ? cachedUser : auth.getCurrentUser();
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}
