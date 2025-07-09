package de.omagh.lumibuddy.feature_diary;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link DiaryRepository} using an in-memory Room DB.
 */
@RunWith(AndroidJUnit4.class)
public class DiaryRepositoryIntegrationTest {
    private AppDatabase db;
    private DiaryRepository repository;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        repository = new DiaryRepository(db.diaryDao());
    }

    @After
    public void teardown() {
        repository.shutdown();
        db.close();
    }

    /**
     * Inserts and retrieves a diary entry for a plant.
     */
    @Test
    public void insertAndFetchEntry() throws Exception {
        DiaryEntry entry = new DiaryEntry("1", "p1", 1L, "note", "", "event");
        repository.insert(entry);
        LiveData<List<DiaryEntry>> live = repository.getEntriesForPlant("p1");
        List<DiaryEntry> result = getValue(live);
        assertEquals(1, result.size());
        assertEquals("note", result.get(0).getNote());
    }

    private <T> T getValue(LiveData<T> live) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        Observer<T> obs = new Observer<T>() {
            @Override
            public void onChanged(T t) {
                data[0] = t;
                latch.countDown();
                live.removeObserver(this);
            }
        };
        live.observeForever(obs);
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }
}