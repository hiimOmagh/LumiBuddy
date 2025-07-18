package de.omagh.core_data.repository.firebase;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.omagh.core_domain.model.Plant;

/**
 * Lightweight DAO wrapper around the "plants" Firestore collection.
 */
public class FirestorePlantDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String uid;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FirestorePlantDao(String uid) {
        this.uid = uid;
    }

    private com.google.firebase.firestore.CollectionReference collection() {
        return db.collection("users").document(uid).collection("plants");
    }

    /**
     * Returns a LiveData emitting error messages.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Returns a LiveData list of all plants.
     */
    public LiveData<List<Plant>> getAll() {
        MutableLiveData<List<Plant>> liveData = new MutableLiveData<>();
        collection().addSnapshotListener((snap, e) -> {
            if (e != null) {
                errorLiveData.postValue(e.getMessage());
                return;
            }
            List<Plant> list = new ArrayList<>();
            if (snap != null) {
                for (DocumentSnapshot d : snap.getDocuments()) {
                    list.add(fromDoc(d));
                }
            }
            liveData.postValue(list);
        });
        return liveData;
    }

    /**
     * Returns a list of all plants synchronously.
     */
    public List<Plant> getAllSync() {
        try {
            QuerySnapshot snap = Tasks.await(collection().get());
            List<Plant> list = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) {
                list.add(fromDoc(d));
            }
            return list;
        } catch (Exception e) {
            errorLiveData.postValue(e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Returns a LiveData of a single plant by id.
     */
    public LiveData<Plant> getById(String id) {
        MutableLiveData<Plant> liveData = new MutableLiveData<>();
        collection().document(id).addSnapshotListener((snap, e) -> {
            if (e != null) {
                errorLiveData.postValue(e.getMessage());
                return;
            }
            if (snap != null && snap.exists()) {
                liveData.postValue(fromDoc(snap));
            }
        });
        return liveData;
    }

    /**
     * Inserts or replaces a plant.
     */
    public void insert(Plant plant) {
        collection()
                .document(plant.getId())
                .set(toMap(plant))
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    /**
     * Updates a plant. Alias for insert in Firestore.
     */
    public void update(Plant plant) {
        insert(plant);
    }

    /**
     * Deletes a plant document.
     */
    public void delete(Plant plant) {
        collection()
                .document(plant.getId())
                .delete()
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    private Plant fromDoc(DocumentSnapshot doc) {
        String id = doc.getString("id");
        String name = doc.getString("name");
        String type = doc.getString("type");
        String imageUri = doc.getString("imageUri");
        Long updated = doc.getLong("updatedAt");
        if (updated == null) updated = 0L;
        return new Plant(id, name, type, imageUri, updated);
    }

    private Map<String, Object> toMap(Plant plant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", plant.getId());
        map.put("name", plant.getName());
        map.put("type", plant.getType());
        map.put("imageUri", plant.getImageUri());
        map.put("updatedAt", plant.getUpdatedAt());
        return map;
    }
}
