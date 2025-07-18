package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;

import de.omagh.core_data.repository.firebase.FirestorePlantDao;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * Worker that uploads a single plant to Firestore.
 */
public class UploadPlantWorker extends Worker {
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_TYPE = "type";
    public static final String KEY_IMAGE_URI = "imageUri";
    /*
        public static final String KEY_UPDATED = "updated";
    */
    public static final String KEY_UPDATED_AT = "updatedAt";

    private final FirebaseManager firebaseManager;

    public UploadPlantWorker(@NonNull Context context,
                             @NonNull WorkerParameters params,
                             FirebaseManager firebaseManager) {
        super(context, params);
        this.firebaseManager = firebaseManager;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Tasks.await(firebaseManager.signInAnonymously());
        } catch (Exception e) {
            return Result.retry();
        }
        Data d = getInputData();
        Plant plant = new Plant(
                d.getString(KEY_ID),
                d.getString(KEY_NAME),
                d.getString(KEY_TYPE),
                d.getString(KEY_IMAGE_URI),
                d.getLong(KEY_UPDATED_AT, System.currentTimeMillis())
/*                d.getString(KEY_IMAGE_URI),
                d.getLong(KEY_UPDATED, System.currentTimeMillis())*/
        );
        FirestorePlantDao dao = new FirestorePlantDao(firebaseManager.getUser().getUid());
        dao.insert(plant);
        return Result.success();
    }
}
