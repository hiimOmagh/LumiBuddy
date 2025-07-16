package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AddPlantViewModelTest {
    @Mock
    PlantIdentifier identifier;
    @Mock
    PlantIdRepository idRepo;

    private AddPlantViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(identifier.identifyPlant(Mockito.any())).thenReturn(new MutableLiveData<>(new Prediction("id", 0.9f)));
        Mockito.when(idRepo.identifyPlant(Mockito.any())).thenReturn(new MutableLiveData<>(new PlantIdSuggestion("c", "s")));
        vm = new AddPlantViewModel(app, identifier, idRepo);
    }

    @Test
    public void identifyPlant_returnsIdentifierLiveData() {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        assertSame(identifier.identifyPlant(bmp), vm.identifyPlant(bmp));
    }

    @Test
    public void identifyPlantWithApi_usesRepositoryWhenUnknown() {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        MutableLiveData<Prediction> local = new MutableLiveData<>(new Prediction(null, 0.1f));
        Mockito.when(identifier.identifyPlant(bmp)).thenReturn(local);
        MutableLiveData<PlantIdSuggestion> remote = new MutableLiveData<>(new PlantIdSuggestion("rose", "rosa"));
        Mockito.when(idRepo.identifyPlant(bmp)).thenReturn(remote);
        vm.identifyPlantWithApi(bmp);
        Mockito.verify(idRepo).identifyPlant(bmp);
    }
}
