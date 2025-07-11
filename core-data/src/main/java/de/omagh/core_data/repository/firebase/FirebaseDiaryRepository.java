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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryDataSource;

/**
 * Firestore-backed implementation of {@link DiaryDataSource}.
 */
public class FirebaseDiaryRepository implements DiaryDataSource {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
        MutableLiveData<List<DiaryEntry>> liveData = new MutableLiveData<>();
        db.collection("diary_entries")
                .whereEqualTo("plantId", plantId)
                .addSnapshotListener((snap, e) -> {
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

    public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
        try {
            QuerySnapshot snap = Tasks.await(db.collection("diary_entries")
                    .whereEqualTo("plantId", plantId)
                    .get());
            List<DiaryEntry> list = new ArrayList<>();
            for (DocumentSnapshot d : snap.getDocuments()) {
                list.add(fromDoc(d));
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void insert(DiaryEntry entry) {
        executor.execute(() -> db.collection("diary_entries")
                .document(entry.getId())
                .set(toMap(entry)));
    }

    public void delete(DiaryEntry entry) {
        executor.execute(() -> db.collection("diary_entries")
                .document(entry.getId())
                .delete());
    }

    private DiaryEntry fromDoc(DocumentSnapshot doc) {
        String id = doc.getString("id");
        String plantId = doc.getString("plantId");
        Long timestamp = doc.getLong("timestamp");
        String note = doc.getString("note");
        String imageUri = doc.getString("imageUri");
        String eventType = doc.getString("eventType");
        if (timestamp == null) timestamp = 0L;
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
