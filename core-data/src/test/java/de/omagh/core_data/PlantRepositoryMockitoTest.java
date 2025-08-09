package de.omagh.core_data;

import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
    
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.repository.Result;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.sync.SyncScheduler;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Additional Mockito based tests for {@link PlantRepository}.
 */
public class PlantRepositoryMockitoTest {
    @Mock
    AppDatabase db;
    @Mock
    PlantDao dao;
    @Mock
    SyncScheduler scheduler;
    
    private PlantRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(db.plantDao()).thenReturn(dao);
        AppExecutors executors = Mockito.mock(AppExecutors.class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Mockito.when(executors.single()).thenReturn(executor);
        android.content.Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        repository = new PlantRepository(context, db, executors, scheduler);
    }

    @Test
    public void getPlant_delegatesToDao() {
        MutableLiveData<de.omagh.core_data.model.Plant> live = new MutableLiveData<>();
        live.setValue(new de.omagh.core_data.model.Plant("1", "Rose", "Flower", ""));
        Mockito.when(dao.getById("1")).thenReturn(live);

        LiveData<Plant> result = repository.getPlant("1");
        result.observeForever(p -> {
        });
        Plant plant = result.getValue();

        Mockito.verify(dao).getById("1");
        assertNotNull(plant);
        assertEquals("Rose", plant.getName());
    }

    @Test
    public void getAllPlants_transformsEntities() {
        List<de.omagh.core_data.model.Plant> entities = List.of(
                new de.omagh.core_data.model.Plant("1", "Tomato", "Solanum", "")
        );
        MutableLiveData<List<de.omagh.core_data.model.Plant>> live = new MutableLiveData<>(entities);
        Mockito.when(dao.getAll()).thenReturn(live);

        LiveData<List<Plant>> result = repository.getAllPlants();
        result.observeForever(list -> {
        });

        Mockito.verify(dao).getAll();
        List<Plant> plants = result.getValue();
        assert plants != null;
        assertEquals(1, plants.size());
        assertEquals("Tomato", plants.get(0).getName());
    }

    @Test
    public void insertPlant_callsDaoAndSchedulesSync() throws Exception {
        Plant plant = new Plant("1", "Rose", "Flower", "");

        LiveData<Result<Void>> live = repository.insertPlant(plant);
        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        live.observeForever(r -> { resultRef.set(r); latch.countDown(); });
        latch.await(2, TimeUnit.SECONDS);

        Mockito.verify(dao).insert(Mockito.any(de.omagh.core_data.model.Plant.class));
        Mockito.verify(scheduler).scheduleDaily();

        Result<Void> result = resultRef.get();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void updatePlant_callsDaoAndSchedulesSync() throws Exception {
        Plant plant = new Plant("1", "Rose", "Flower", "");

        LiveData<Result<Void>> live = repository.updatePlant(plant);
        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        live.observeForever(r -> { resultRef.set(r); latch.countDown(); });
        latch.await(2, TimeUnit.SECONDS);

        Mockito.verify(dao).update(Mockito.any(de.omagh.core_data.model.Plant.class));
        Mockito.verify(scheduler).scheduleDaily();

        Result<Void> result = resultRef.get();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void deletePlant_callsDaoAndSchedulesSync() throws Exception {
        Plant plant = new Plant("1", "Rose", "Flower", "");

        LiveData<Result<Void>> live = repository.deletePlant(plant);
        AtomicReference<Result<Void>> resultRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        live.observeForever(r -> { resultRef.set(r); latch.countDown(); });
        latch.await(2, TimeUnit.SECONDS);

        Mockito.verify(dao).delete(Mockito.any(de.omagh.core_data.model.Plant.class));
        Mockito.verify(scheduler).scheduleDaily();

        Result<Void> result = resultRef.get();
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    public void getAllPlantsSync_returnsDomainModels() {
        List<de.omagh.core_data.model.Plant> entities = Arrays.asList(
                new de.omagh.core_data.model.Plant("1", "Rose", "Flower", ""),
                new de.omagh.core_data.model.Plant("2", "Tulip", "Flower", "")
        );
        Mockito.when(dao.getAllSync()).thenReturn(entities);

        Result<List<Plant>> result = repository.getAllPlantsSync();

        Mockito.verify(dao).getAllSync();
        assertNotNull(result);
        assertTrue(result.isSuccess());
        List<Plant> plants = result.getData();
        assertNotNull(plants);
        assertEquals(2, plants.size());
        assertEquals("Rose", plants.get(0).getName());
    }

    @Test
    public void getAllPlantsSync_handlesException() {
        Mockito.when(dao.getAllSync()).thenThrow(new RuntimeException("db"));

        Result<List<Plant>> result = repository.getAllPlantsSync();

        Mockito.verify(dao).getAllSync();
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
    }
}
