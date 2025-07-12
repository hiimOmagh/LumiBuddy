package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;
import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.shared_ml.PlantIdentifier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class AddPlantViewModelTest {
    @Mock
    PlantIdentifier identifier;

    private AddPlantViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(identifier.identifyPlant(Mockito.any())).thenReturn(new MutableLiveData<>("id"));
        vm = new AddPlantViewModel(app, identifier);
    }

    @Test
    public void identifyPlant_returnsIdentifierLiveData() {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        assertSame(identifier.identifyPlant(bmp), vm.identifyPlant(bmp));
    }
}
