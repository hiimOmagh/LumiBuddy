package de.omagh.lumibuddy.feature_plantdb;

import androidx.lifecycle.MutableLiveData;

import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.PlantDao;
import de.omagh.core_domain.model.Plant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

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
        repository = new PlantRepository(db);
    }

    /**
     * Verifies that inserting a plant delegates to the DAO.
     */
    @Test
    public void insertPlant_callsDao() {
        de.omagh.core_data.model.Plant p = new de.omagh.core_data.model.Plant("1", "Tomato", "Solanum", "");
        repository.insertPlant(p);
        Mockito.verify(dao, Mockito.timeout(1000)).insert(p);
    }

    /**
     * Verifies that updating a plant delegates to the DAO.
     */
    @Test
    public void updatePlant_callsDao() {
        Plant p = new de.omagh.core_data.model.Plant("1", "Tomato", "Solanum", "");
        repository.updatePlant(p);
        Mockito.verify(dao, Mockito.timeout(1000)).update(p);
    }

    /**
     * Verifies that deleting a plant delegates to the DAO.
     */
    @Test
    public void deletePlant_callsDao() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.deletePlant(p);
        Mockito.verify(dao, Mockito.timeout(1000)).delete(p);
    }

    /**
     * Ensures the LiveData returned by getAllPlants is from the DAO.
     */
    @Test
    public void getAllPlants_returnsDaoLiveData() {
        MutableLiveData<List<Plant>> live = new MutableLiveData<>();
        Mockito.when(dao.getAll()).thenReturn(live);
        assertSame(live, repository.getAllPlants());
    }

    /**
     * Ensures duplicate inserts are forwarded to the DAO each time.
     */
    @Test
    public void insertDuplicate_callsDaoEachTime() {
        Plant p = new Plant("1", "Tomato", "Solanum", "");
        repository.insertPlant(p);
        repository.insertPlant(p);
        Mockito.verify(dao, Mockito.timeout(1000).times(2)).insert(p);
    }
}