package de.omagh.feature_plantdb;

import static org.junit.Assert.assertSame;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.plantdb.PlantInfo;
import de.omagh.feature_plantdb.ui.PlantListViewModel;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.plantdb.PlantInfoRepository;

/**
 * Tests for {@link PlantListViewModel#getAllPlantInfo()} using a mocked database.
 */
public class PlantListViewModelMockitoTest {
    @Mock
    PlantDatabaseManager db;

    @Mock
    PlantInfoRepository infoRepo;

    @Mock
    PlantRepository repo;

    private PlantListViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        LiveData<List<Plant>> dummy = new MutableLiveData<>(Collections.emptyList());
        Mockito.when(repo.getAllPlants()).thenReturn(dummy);
        vm = new PlantListViewModel(app, infoRepo, db, repo);
    }

    @Test
    public void getAllPlantInfo_returnsFromDatabase() throws Exception {
        List<PlantInfo> list = Collections.emptyList();
        Mockito.when(db.getAllPlants()).thenReturn(list);
        assertSame(list, vm.getAllPlantInfo());
    }
}