package de.omagh.lumibuddy.feature_plantdb;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.feature_plantdb.ui.AddPlantFragment;
import de.omagh.feature_plantdb.ui.AddPlantViewModel;
import de.omagh.feature_plantdb.ui.PlantDbViewModelFactory;
import de.omagh.core_infra.plantdb.PlantIdentificationUseCase;
import de.omagh.shared_ml.PlantIdentifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Verifies that {@link AddPlantFragment} shows the "identification_failed" toast when
 * {@link PlantIdentificationUseCase} returns {@code null}.
 */
@RunWith(AndroidJUnit4.class)
public class AddPlantFailureTest {
    static PlantIdentifier identifier;
    static PlantIdentificationUseCase useCase;

    @Before
    public void setup() {
        identifier = mock(PlantIdentifier.class);
        useCase = mock(PlantIdentificationUseCase.class);
        doReturn(new MutableLiveData<>(null)).when(useCase).identify(any());
    }

    @Test
    public void identifyFailure_showsToast() {
        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(TestFragment.class);
        final Activity[] activity = new Activity[1];
        scenario.onFragment(f -> {
            activity[0] = f.requireActivity();
            Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            try {
                java.lang.reflect.Method m = AddPlantFragment.class.getDeclaredMethod("identifyWithApi", Bitmap.class);
                m.setAccessible(true);
                m.invoke(f, bmp);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Espresso.onView(ViewMatchers.withText(de.omagh.feature_plantdb.R.string.identification_failed))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /** Test fragment injecting a failing {@link PlantIdentificationUseCase}. */
    public static class TestFragment extends AddPlantFragment {
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.viewModelFactory = new PlantDbViewModelFactory(() -> null,
                    () -> new AddPlantViewModel(new android.app.Application(), identifier, useCase),
                    () -> null);
        }
    }
}

