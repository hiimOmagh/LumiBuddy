package de.omagh.lumibuddy.feature_plantdb;

import androidx.lifecycle.MutableLiveData;

import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link PlantRepository}.
 */
public class PlantRepositoryTest {
    @Mock
    private AppDatabase db;
    @Mock
    private PlantDao dao;

    private PlantRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(db.plantDao()).thenReturn(dao);
        AppExecutors executors = Mockito.mock(AppExecutors.class);
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        Mockito.when(executors.single()).thenReturn(executor);
        android.content.Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        repository = new PlantRepository(context, db, executors);
    }

    /**
     * Verifies that inserting a plant delegates to the DAO.
     */
    @Test
    public void insertPlant_callsDao() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.insertPlant(p);
        Mockito.verify(dao, Mockito.timeout(1000))
                .insert(Mockito.any(de.omagh.core_data.model.Plant.class));
    }

    /**
     * Verifies that updating a plant delegates to the DAO.
     */
    @Test
    public void updatePlant_callsDao() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.updatePlant(p);
        Mockito.verify(dao, Mockito.timeout(1000))
                .update(Mockito.any(de.omagh.core_data.model.Plant.class));
    }

    /**
     * Verifies that deleting a plant delegates to the DAO.
     */
    @Test
    public void deletePlant_callsDao() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.deletePlant(p);
        Mockito.verify(dao, Mockito.timeout(1000))
                .delete(Mockito.any(de.omagh.core_data.model.Plant.class));
    }

    /**
     * Ensures the LiveData returned by getAllPlants is from the DAO.
     */
    @Test
    public void getAllPlants_returnsDaoLiveData() {
        MutableLiveData<List<de.omagh.core_data.model.Plant>> live = new MutableLiveData<>();
        Mockito.when(dao.getAll()).thenReturn(live);
        repository.getAllPlants();
        Mockito.verify(dao, Mockito.timeout(1000)).getAll();
    }

    /**
     * getPlant should query the DAO by id and transform the entity.
     */
    @Test
    public void getPlant_returnsMappedLiveData() {
        MutableLiveData<de.omagh.core_data.model.Plant> live = new MutableLiveData<>();
        live.setValue(new de.omagh.core_data.model.Plant("2", "Rose", "Flower", ""));
        Mockito.when(dao.getById("2")).thenReturn(live);

        androidx.lifecycle.LiveData<Plant> result = repository.getPlant("2");
        result.observeForever(p -> {
        });

        Mockito.verify(dao).getById("2");
        assertEquals("Rose", Objects.requireNonNull(result.getValue()).getName());
    }

    /**
     * Ensures duplicate inserts are forwarded to the DAO each time.
     */
    @Test
    public void insertDuplicate_callsDaoEachTime() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.insertPlant(p);
        repository.insertPlant(p);
        Mockito.verify(dao, Mockito.timeout(1000).times(2))
                .insert(Mockito.any(de.omagh.core_data.model.Plant.class));
    }
}