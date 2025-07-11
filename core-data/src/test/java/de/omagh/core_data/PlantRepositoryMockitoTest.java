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

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Additional Mockito based tests for {@link PlantRepository}.
 */
public class PlantRepositoryMockitoTest {
    @Mock
    AppDatabase db;
    @Mock
    PlantDao dao;

    private PlantRepository repository;
    private AppExecutors executors;
    private ExecutorService executor;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Mockito.when(db.plantDao()).thenReturn(dao);
        executors = Mockito.mock(AppExecutors.class);
        executor = Executors.newSingleThreadExecutor();
        Mockito.when(executors.single()).thenReturn(executor);
        repository = new PlantRepository(db, executors);
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
        List<de.omagh.core_data.model.Plant> entities = Arrays.asList(
                new de.omagh.core_data.model.Plant("1", "Tomato", "Solanum", "")
        );
        MutableLiveData<List<de.omagh.core_data.model.Plant>> live = new MutableLiveData<>(entities);
        Mockito.when(dao.getAll()).thenReturn(live);

        LiveData<List<Plant>> result = repository.getAllPlants();
        result.observeForever(list -> {
        });

        Mockito.verify(dao).getAll();
        List<Plant> plants = result.getValue();
        assertEquals(1, plants.size());
        assertEquals("Tomato", plants.get(0).getName());
    }
}
