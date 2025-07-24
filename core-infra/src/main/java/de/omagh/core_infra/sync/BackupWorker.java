package de.omagh.core_infra.sync;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.ArrayList;
import java.util.List;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_infra.sync.WorkSyncScheduler;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Worker that enqueues upload tasks for local plants and diary entries.
 */
public class BackupWorker extends Worker {
    public BackupWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(context);
        PlantRepository plantRepo = new PlantRepository(context, db, new AppExecutors(), new WorkSyncScheduler(context));
        List<Plant> plants = plantRepo.getAllPlantsSync();
        if (plants == null) plants = new ArrayList<>();
        WorkManager wm = WorkManager.getInstance(context);
        for (Plant p : plants) {
            Data data = new Data.Builder()
                    .putString(UploadPlantWorker.KEY_ID, p.getId())
                    .putString(UploadPlantWorker.KEY_NAME, p.getName())
                    .putString(UploadPlantWorker.KEY_TYPE, p.getType())
                    .putString(UploadPlantWorker.KEY_IMAGE_URI, p.getImageUri())
                    .putLong(UploadPlantWorker.KEY_UPDATED_AT, p.getUpdatedAt())
                    .build();
            wm.enqueue(new OneTimeWorkRequest.Builder(UploadPlantWorker.class)
                    .setInputData(data)
                    .build());
        }
        List<DiaryEntry> entries = db.diaryDao().getAllEntriesSync();
        for (DiaryEntry e : entries) {
            Data data = new Data.Builder()
                    .putString(UploadDiaryEntryWorker.KEY_ID, e.getId())
                    .putString(UploadDiaryEntryWorker.KEY_PLANT_ID, e.getPlantId())
                    .putLong(UploadDiaryEntryWorker.KEY_TIMESTAMP, e.getTimestamp())
                    .putString(UploadDiaryEntryWorker.KEY_NOTE, e.getNote())
                    .putString(UploadDiaryEntryWorker.KEY_IMAGE_URI, e.getImageUri())
                    .putString(UploadDiaryEntryWorker.KEY_EVENT_TYPE, e.getEventType())
                    .build();
            wm.enqueue(new OneTimeWorkRequest.Builder(UploadDiaryEntryWorker.class)
                    .setInputData(data)
                    .build());
        }
        return Result.success();
    }
}