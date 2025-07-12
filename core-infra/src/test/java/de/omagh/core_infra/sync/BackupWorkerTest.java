package de.omagh.core_infra.sync;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.Configuration;
import androidx.work.WorkManager;
import androidx.work.testing.TestListenableWorkerBuilder;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * Tests {@link BackupWorker} end-to-end with injected upload repositories.
 */
public class BackupWorkerTest {
    private Context context;
    private AppDatabase db;

    @Before
    public void setup() throws Exception {
        context = ApplicationProvider.getApplicationContext();
        db = androidx.room.Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        Field f = AppDatabase.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, db);
    }

    @After
    public void teardown() throws Exception {
        db.close();
        Field f = AppDatabase.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
    }

    @Test
    public void backupWorker_enqueuesUploads() throws Exception {
        // Insert sample local data
        db.plantDao().insert(new de.omagh.core_data.model.Plant("1", "Rose", "Flower", "img"));
        db.diaryDao().insert(new DiaryEntry("d1", "1", 1L, "note", "img", "watering"));

        PlantDataSource remotePlant = Mockito.mock(PlantDataSource.class);
        DiaryDataSource remoteDiary = Mockito.mock(DiaryDataSource.class);
        FirebaseManager firebase = Mockito.mock(FirebaseManager.class);
        Mockito.when(firebase.signInAnonymously()).thenReturn(Tasks.forResult(null));

        SyncWorkerFactory factory = new SyncWorkerFactory(() -> remotePlant, () -> remoteDiary, () -> firebase);
        Configuration config = new Configuration.Builder()
                .setWorkerFactory(factory)
                .build();
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);

        BackupWorker worker = TestListenableWorkerBuilder.from(context, BackupWorker.class)
                .build();
        worker.startWork().get();

        // Verify upload workers invoked remote repositories
        Mockito.verify(remotePlant, Mockito.timeout(5000)).insertPlant(Mockito.any(Plant.class));
        Mockito.verify(remoteDiary, Mockito.timeout(5000)).insert(Mockito.any(DiaryEntry.class));

        WorkManager.getInstance(context).cancelAllWork();
    }
}
