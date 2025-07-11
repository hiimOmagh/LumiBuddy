package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.firebase.FirebaseDiaryRepository;

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

    public UploadDiaryEntryWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data data = getInputData();
        DiaryEntry entry = new DiaryEntry(
                data.getString(KEY_ID),
                data.getString(KEY_PLANT_ID),
                data.getLong(KEY_TIMESTAMP, 0),
                data.getString(KEY_NOTE),
                data.getString(KEY_IMAGE_URI),
                data.getString(KEY_EVENT_TYPE)
        );
        new FirebaseDiaryRepository().insert(entry);
        return Result.success();
    }
}
