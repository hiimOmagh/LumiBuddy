package de.omagh.lumibuddy.feature_plantdb;

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

import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_domain.model.Plant;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link PlantRepository} with an in-memory database.
 */
@RunWith(AndroidJUnit4.class)
public class PlantRepositoryIntegrationTest {
    private AppDatabase db;
    private PlantRepository repository;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).allowMainThreadQueries().build();
        repository = new PlantRepository(db);
    }

    @After
    public void teardown() {
        db.close();
    }

    /**
     * Inserting and retrieving a plant should work end-to-end.
     */
    @Test
    public void insertAndFetchPlant() throws Exception {
        Plant plant = new Plant("1", "Basil", "Basil", "");
        repository.insertPlant(plant);
        LiveData<List<Plant>> live = repository.getAllPlants();
        List<Plant> result = getValue(live);
        assertEquals(1, result.size());
        assertEquals("Basil", result.get(0).getName());
    }

    // Helper to synchronously get LiveData value
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