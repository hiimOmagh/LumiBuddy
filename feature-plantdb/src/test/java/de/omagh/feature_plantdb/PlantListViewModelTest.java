package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

import org.mockito.Mockito;

import org.junit.Test;

public class PlantListViewModelTest {
    @Test
    public void getPlants_returnsLiveData() {
        Application app = ApplicationProvider.getApplicationContext();
        PlantInfoRepository infoRepo = Mockito.mock(PlantInfoRepository.class);
        PlantDatabaseManager dbManager = Mockito.mock(PlantDatabaseManager.class);
        PlantRepository repo = Mockito.mock(PlantRepository.class);

        LiveData<java.util.List<Plant>> dummy = new MutableLiveData<>(java.util.Collections.emptyList());
        Mockito.when(repo.getAllPlants()).thenReturn(dummy);

        PlantListViewModel vm = new PlantListViewModel(app, infoRepo, dbManager, repo);
        assertSame(dummy, vm.getPlants());
    }
}
