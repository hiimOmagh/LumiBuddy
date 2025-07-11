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

import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_domain.model.Plant;

/**
 * Remote {@link PlantDataSource} implementation backed by Firebase Firestore.
 */
public class FirebasePlantRepository implements PlantDataSource {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public LiveData<List<Plant>> getAllPlants() {
        MutableLiveData<List<Plant>> liveData = new MutableLiveData<>();
        db.collection("plants").addSnapshotListener((snap, e) -> {
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

    public LiveData<Plant> getPlant(String id) {
        MutableLiveData<Plant> liveData = new MutableLiveData<>();
        db.collection("plants").document(id).addSnapshotListener((snap, e) -> {
            if (snap != null && snap.exists()) {
                liveData.postValue(fromDoc(snap));
            }
        });
        return liveData;
    }

    public void insertPlant(Plant plant) {
        executor.execute(() -> db.collection("plants").document(plant.getId()).set(toMap(plant)));
    }

    public void updatePlant(Plant plant) {
        insertPlant(plant);
    }

    public void deletePlant(Plant plant) {
        executor.execute(() -> db.collection("plants").document(plant.getId()).delete());
    }

    private Plant fromDoc(DocumentSnapshot doc) {
        String id = doc.getString("id");
        String name = doc.getString("name");
        String type = doc.getString("type");
        String imageUri = doc.getString("imageUri");
        return new Plant(id, name, type, imageUri);
    }

    private Map<String, Object> toMap(Plant plant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", plant.getId());
        map.put("name", plant.getName());
        map.put("type", plant.getType());
        map.put("imageUri", plant.getImageUri());
        return map;
    }
}