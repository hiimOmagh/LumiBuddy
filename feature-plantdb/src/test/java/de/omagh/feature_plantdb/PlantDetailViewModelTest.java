package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.plantdb.PlantInfoRepository;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.core_infra.plantdb.PlantIdRepository;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

public class PlantDetailViewModelTest {
    @Mock
    PlantInfoRepository infoRepo;
    @Mock
    PlantRepository repo;
    @Mock
    PlantIdentifier identifier;
    @Mock
    PlantIdRepository idRepo;

    private PlantDetailViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(infoRepo.searchSpecies(Mockito.anyString())).thenReturn(new MutableLiveData<>(Collections.emptyList()));
        Mockito.when(infoRepo.getCareProfile(Mockito.anyString())).thenReturn(new MutableLiveData<>(Collections.emptyList()));
        Mockito.when(identifier.identifyPlant(Mockito.any())).thenReturn(new MutableLiveData<>(new Prediction("id", 0.9f)));
        Mockito.when(idRepo.identifyPlant(Mockito.any())).thenReturn(new MutableLiveData<>(new PlantIdSuggestion("c", "s")));
        vm = new PlantDetailViewModel(app, infoRepo, repo, identifier, idRepo);
    }

    @Test
    public void setPlant_updatesLiveData() {
        Plant p = new Plant("1", "N", "T", "");
        vm.setPlant(p);
        assertEquals(p, vm.getPlant().getValue());
    }

    @Test
    public void savePlant_callsRepository() {
        Plant p = new Plant("2", "N", "T", "");
        vm.setPlant(p);
        vm.savePlant();
        Mockito.verify(repo).updatePlant(p);
    }

    @Test
    public void searchSpecies_returnsRepoLiveData() {
        LiveData<List<PlantSpecies>> live = vm.searchSpecies("a");
        assertSame(live, infoRepo.searchSpecies("a"));
    }

    @Test
    public void getCareProfile_returnsRepoLiveData() {
        LiveData<List<PlantCareProfileEntity>> live = vm.getCareProfile("id");
        assertSame(live, infoRepo.getCareProfile("id"));
    }

    @Test
    public void identifyPlantWithApi_usesRepositoryWhenUnknown() {
        android.graphics.Bitmap bmp = android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888);
        MutableLiveData<Prediction> local = new MutableLiveData<>(new Prediction(null, 0.1f));
        Mockito.when(identifier.identifyPlant(bmp)).thenReturn(local);
        MutableLiveData<PlantIdSuggestion> remote = new MutableLiveData<>(new PlantIdSuggestion("rose", "rosa"));
        Mockito.when(idRepo.identifyPlant(bmp)).thenReturn(remote);
        vm.identifyPlantWithApi(bmp);
        Mockito.verify(idRepo).identifyPlant(bmp);
    }
}
