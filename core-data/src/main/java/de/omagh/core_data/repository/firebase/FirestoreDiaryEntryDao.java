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

import de.omagh.core_data.model.DiaryEntry;

/**
 * DAO wrapper around the "diary_entries" Firestore collection.
 */
public class FirestoreDiaryEntryDao {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String uid;
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public FirestoreDiaryEntryDao(String uid) {
        this.uid = uid;
    }

    private com.google.firebase.firestore.CollectionReference collection() {
        return db.collection("users").document(uid).collection("diary_entries");
    }

    /**
     * Returns a LiveData emitting error messages.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Returns all diary entries as LiveData.
     */
    public LiveData<List<DiaryEntry>> getAll() {
        MutableLiveData<List<DiaryEntry>> liveData = new MutableLiveData<>();
        collection().addSnapshotListener((snap, e) -> {
            if (e != null) {
                errorLiveData.postValue(e.getMessage());
                return;
            }
            List<DiaryEntry> list = new ArrayList<>();
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
     * Returns all diary entries synchronously.
     */
    public List<DiaryEntry> getAllSync() {
        try {
            QuerySnapshot snap = Tasks.await(collection().get());
            List<DiaryEntry> list = new ArrayList<>();
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
     * Returns all entries for a given plant as LiveData.
     */
    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        MutableLiveData<List<DiaryEntry>> liveData = new MutableLiveData<>();
        collection()
                .whereEqualTo("plantId", plantId)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        errorLiveData.postValue(e.getMessage());
                        return;
                    }
                    List<DiaryEntry> list = new ArrayList<>();
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
     * Blocking retrieval of entries for a plant.
     */
    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        try {
            QuerySnapshot snap = Tasks.await(collection()
                    .whereEqualTo("plantId", plantId)
                    .get());
            List<DiaryEntry> list = new ArrayList<>();
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
     * Inserts or replaces a diary entry.
     */
    public void insert(DiaryEntry entry) {
        collection()
                .document(entry.getId())
                .set(toMap(entry))
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    public void update(DiaryEntry entry) {
        insert(entry);
    }

    /**
     * Deletes a diary entry.
     */
    public void delete(DiaryEntry entry) {
        collection()
                .document(entry.getId())
                .delete()
                .addOnFailureListener(e -> errorLiveData.postValue(e.getMessage()));
    }

    private DiaryEntry fromDoc(DocumentSnapshot doc) {
        String id = doc.getString("id");
        String plantId = doc.getString("plantId");
        Long timestamp = doc.getLong("timestamp");
        String note = doc.getString("note");
        String imageUri = doc.getString("imageUri");
        String eventType = doc.getString("eventType");
        if (timestamp == null) timestamp = 0L;
        assert id != null;
        return new DiaryEntry(id, plantId, timestamp, note, imageUri, eventType);
    }

    private Map<String, Object> toMap(DiaryEntry entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", entry.getId());
        map.put("plantId", entry.getPlantId());
        map.put("timestamp", entry.getTimestamp());
        map.put("note", entry.getNote());
        map.put("imageUri", entry.getImageUri());
        map.put("eventType", entry.getEventType());
        return map;
    }
}
