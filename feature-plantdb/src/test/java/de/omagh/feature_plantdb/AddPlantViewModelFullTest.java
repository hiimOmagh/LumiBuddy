package de.omagh.feature_plantdb;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdRepository;

/**
 * Additional tests for {@link AddPlantViewModel} verifying ML/API fallback and cleanup.
 */
public class AddPlantViewModelFullTest {
    @Mock
    PlantIdentifier identifier;
    @Mock
    PlantIdRepository repository;

    private AddPlantViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        when(identifier.identifyPlant(any())).thenReturn(new MutableLiveData<>(new Prediction("rose", 0.9f)));
        vm = new AddPlantViewModel(app, identifier, repository);
    }

    @Test
    public void identifyPlantWithApi_usesLocalResult_whenHighConfidence() {
        Bitmap bmp = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        MutableLiveData<Prediction> local = new MutableLiveData<>(new Prediction("rose", 0.9f));
        when(identifier.identifyPlant(bmp)).thenReturn(local);
        vm.identifyPlantWithApi(bmp);
        assertEquals("rose", vm.getIdentificationResult().getValue().getCommonName());
        verify(repository, never()).identifyPlant(any());
    }

    @Test
    public void identifyPlantWithApi_fallsBackToApi_whenUnknown() {
        Bitmap bmp = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
        MutableLiveData<Prediction> local = new MutableLiveData<>(new Prediction(null, 0.1f));
        when(identifier.identifyPlant(bmp)).thenReturn(local);
        MutableLiveData<PlantIdSuggestion> remote = new MutableLiveData<>(new PlantIdSuggestion("c","s"));
        when(repository.identifyPlant(bmp)).thenReturn(remote);
        vm.identifyPlantWithApi(bmp);
        verify(repository).identifyPlant(bmp);
    }

    @Test
    public void onCleared_closesIdentifier() {
        vm.onCleared();
        verify(identifier).close();
    }
}
