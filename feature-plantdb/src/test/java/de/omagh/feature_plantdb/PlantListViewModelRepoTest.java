package de.omagh.feature_plantdb;

import static org.junit.Assert.assertSame;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.plantdb.PlantInfo;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

/**
 * Tests that {@link PlantListViewModel} delegates work to {@link PlantRepository} and friends.
 */
public class PlantListViewModelRepoTest {
    @Mock
    PlantInfoRepository infoRepo;
    @Mock
    PlantDatabaseManager db;
    @Mock
    PlantRepository repo;

    private PlantListViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(repo.getAllPlants()).thenReturn(new MutableLiveData<>(Collections.emptyList()));
        vm = new PlantListViewModel(app, infoRepo, db, repo);
    }

    @Test
    public void addPlant_delegatesToRepository() {
        Plant p = new Plant("1", "Rose", "Flower", "");
        vm.addPlant(p);
        Mockito.verify(repo).insertPlant(p);
    }

    @Test
    public void updatePlant_delegatesToRepository() {
        Plant p = new Plant("2", "Tomato", "Solanum", "");
        vm.updatePlant(p);
        Mockito.verify(repo).updatePlant(p);
    }

    @Test
    public void deletePlant_delegatesToRepository() {
        Plant p = new Plant("3", "Lily", "Flower", "");
        vm.deletePlant(p);
        Mockito.verify(repo).deletePlant(p);
    }

    @Test
    public void getPlantById_returnsRepositoryLiveData() {
        MutableLiveData<Plant> live = new MutableLiveData<>();
        Mockito.when(repo.getPlant("1")).thenReturn(live);
        assertSame(live, vm.getPlantById("1"));
    }

    @Test
    public void getAllPlantInfo_comesFromDatabase() {
        java.util.List<PlantInfo> list = Collections.emptyList();
        Mockito.when(db.getAllPlants()).thenReturn(list);
        assertSame(list, vm.getAllPlantInfo());
    }
}