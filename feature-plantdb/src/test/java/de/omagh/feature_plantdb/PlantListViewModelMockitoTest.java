package de.omagh.feature_plantdb;

import static org.junit.Assert.assertSame;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import de.omagh.core_data.plantdb.PlantDatabaseManager;
import de.omagh.core_data.plantdb.PlantInfo;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

/**
 * Tests for {@link PlantListViewModel#getAllPlantInfo()} using a mocked database.
 */
public class PlantListViewModelMockitoTest {
    @Mock
    PlantDatabaseManager db;

    private PlantListViewModel vm;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        vm = new PlantListViewModel(app);
        Field f = PlantListViewModel.class.getDeclaredField("sampleDb");
        f.setAccessible(true);
        f.set(vm, db);
    }

    @Test
    public void getAllPlantInfo_returnsFromDatabase() throws Exception {
        List<PlantInfo> list = Collections.emptyList();
        Mockito.when(db.getAllPlants()).thenReturn(list);
        assertSame(list, vm.getAllPlantInfo());
    }
}