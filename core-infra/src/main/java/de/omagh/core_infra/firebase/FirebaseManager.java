package de.omagh.core_infra.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;

/**
 * Lightweight helper exposing FirebaseAuth and Firestore access.
 */
public class FirebaseManager {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<com.google.firebase.auth.AuthResult> signInAnonymously() {
        return auth.signInAnonymously();
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

    public FirebaseFirestore getDb() {
        return db;
    }
}