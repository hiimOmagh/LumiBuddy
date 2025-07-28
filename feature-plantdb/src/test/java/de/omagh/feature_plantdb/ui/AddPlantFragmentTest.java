package de.omagh.feature_plantdb.ui;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.util.PermissionUtils;
import de.omagh.feature_plantdb.R;
import de.omagh.feature_plantdb.di.PlantDbComponent;
import de.omagh.feature_plantdb.ui.widget.LoadingDialogFragment;
import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.feature_plantdb.ui.PlantDbViewModelFactory;
import de.omagh.shared_ml.PlantIdentifier;
import de.omagh.shared_ml.PlantIdentifier.Prediction;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import de.omagh.core_infra.plantdb.PlantIdRepository;

/**
 * Instrumented tests for {@link AddPlantFragment} verifying progress dialog and permission flows.
 */
@RunWith(AndroidJUnit4.class)
public class AddPlantFragmentTest {
    static PlantIdentifier identifier;
    static PlantIdRepository repo;

    @Before
    public void setup() {
        identifier = mock(PlantIdentifier.class);
        repo = mock(PlantIdRepository.class);
                when(identifier.identifyPlant(any())).thenReturn(new MutableLiveData<>(new Prediction("rose",0.9f)));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void progressDialog_dismissedAfterIdentification() {
        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(TestFragment.class);
        scenario.onFragment(f -> {
            Bitmap bmp = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
            try {
                java.lang.reflect.Method m = AddPlantFragment.class.getDeclaredMethod("identifyWithApi", Bitmap.class);
                m.setAccessible(true);
                m.invoke(f, bmp);
                java.lang.reflect.Field pf = AddPlantFragment.class.getDeclaredField("progressDialog");
                pf.setAccessible(true);
                Object dlg = pf.get(f);
                assertNull(((LoadingDialogFragment) dlg).getDialog());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /** Test fragment bypassing Dagger injection. */
    public static class TestFragment extends AddPlantFragment {
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.viewModelFactory =
            new PlantDbViewModelFactory(() -> null,
                    () -> new AddPlantViewModel(new android.app.Application(), identifier, repo),
                    () -> null);
        }
    }
}
