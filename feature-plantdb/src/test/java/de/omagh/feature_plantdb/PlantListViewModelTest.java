package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import de.omagh.feature_plantdb.ui.PlantListViewModel;

import org.junit.Test;

public class PlantListViewModelTest {
    @Test
    public void getPlants_returnsLiveData() {
        Application app = ApplicationProvider.getApplicationContext();
        PlantListViewModel vm = new PlantListViewModel(app);
        assertNotNull(vm.getPlants());
    }
}
