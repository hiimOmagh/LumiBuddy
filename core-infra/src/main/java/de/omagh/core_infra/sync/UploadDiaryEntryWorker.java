package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;

import java.util.Objects;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.firebase.FirestoreDiaryEntryDao;
import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * Worker that uploads a single diary entry to Firestore.
 */
public class UploadDiaryEntryWorker extends Worker {
    public static final String KEY_ID = "id";
    public static final String KEY_PLANT_ID = "plantId";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_NOTE = "note";
    public static final String KEY_IMAGE_URI = "imageUri";
    public static final String KEY_EVENT_TYPE = "eventType";

    private final FirebaseManager firebaseManager;

    public UploadDiaryEntryWorker(@NonNull Context context,
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
        Data data = getInputData();
        DiaryEntry entry = new DiaryEntry(
                Objects.requireNonNull(data.getString(KEY_ID)),
                data.getString(KEY_PLANT_ID),
                data.getLong(KEY_TIMESTAMP, 0),
                data.getString(KEY_NOTE),
                data.getString(KEY_IMAGE_URI),
                data.getString(KEY_EVENT_TYPE)
        );
        FirestoreDiaryEntryDao dao = new FirestoreDiaryEntryDao(firebaseManager.getUser().getUid());
        dao.insert(entry);
        return Result.success();
    }
}
