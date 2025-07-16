package de.omagh.feature_measurement;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.feature_measurement.infra.GrowLightProductRepository;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.feature_measurement.ui.LampProfilesViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LampProfilesViewModelTest {
    @Mock
    GrowLightProfileManager manager;
    @Mock
    GrowLightProductRepository repo;

    private LampProfilesViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(manager.getAllProfiles()).thenReturn(Collections.emptyList());
        Mockito.when(repo.searchGrowLights(Mockito.anyString())).thenReturn(new MutableLiveData<>(Collections.emptyList()));
        vm = new LampProfilesViewModel(app, manager, repo);
    }

    @Test
    public void getProfiles_returnsManagerData() {
        assertTrue(Objects.requireNonNull(vm.getProfiles().getValue()).isEmpty());
    }

    @Test
    public void searchProducts_returnsRepoLiveData() {
        assertSame(repo.searchGrowLights("q"), vm.searchProducts("q"));
    }

    @Test
    public void addProfile_updatesManager() {
        LampProduct p = new LampProduct("1", "", "", "", "", 0, 1f, 0f);
        vm.addProfile(p);
        Mockito.verify(manager).addCustomProfile(p);
    }
}
