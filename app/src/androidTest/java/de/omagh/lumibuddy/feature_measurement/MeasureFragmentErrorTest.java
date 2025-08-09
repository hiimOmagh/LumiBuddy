package de.omagh.lumibuddy.feature_measurement;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.feature_measurement.ui.MeasureFragment;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_measurement.ui.MeasureViewModelFactory;
import de.omagh.feature_measurement.ui.MeasurementController;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Ensures that {@link MeasureFragment} shows a camera error toast when
 * {@link MeasurementController} reports an error.
 */
@RunWith(AndroidJUnit4.class)
public class MeasureFragmentErrorTest {

    @Test
    public void measurementError_showsToast() {
        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(TestFragment.class);

        Espresso.onView(ViewMatchers.withId(de.omagh.feature_measurement.R.id.cameraMeasureButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withText("OK")).perform(ViewActions.click());

        final Activity[] activity = new Activity[1];
        scenario.onFragment(f -> activity[0] = f.requireActivity());

        Espresso.onView(ViewMatchers.withText("Camera error: test failure"))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    /** Fragment replacing {@link MeasurementController} with a fake that triggers onError. */
    public static class TestFragment extends MeasureFragment {
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            MeasureViewModel vm = mock(MeasureViewModel.class);
            when(vm.isArOverlayEnabled()).thenReturn(false);
            when(vm.getLampProfiles()).thenReturn(java.util.Collections.emptyList());
            when(vm.getPreferredUnit()).thenReturn("Lux");
            when(vm.getLampProfileId()).thenReturn(new MutableLiveData<>("id"));
            when(vm.isAutoSunlightEstimationEnabled()).thenReturn(false);
            when(vm.isMlFeaturesEnabled()).thenReturn(false);
            when(vm.getLux()).thenReturn(new MutableLiveData<>(0f));
            when(vm.getPPFD()).thenReturn(new MutableLiveData<>(0f));
            when(vm.getDLI()).thenReturn(new MutableLiveData<>(0f));
            when(vm.getHours()).thenReturn(new MutableLiveData<>(24));
            when(vm.getCalibrationFactor()).thenReturn(new MutableLiveData<>(1f));
            this.viewModelFactory = new MeasureViewModelFactory(() -> vm);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable android.os.Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            MeasurementController controller = mock(MeasurementController.class);
            doAnswer(invocation -> {
                MeasurementController.Callback cb = invocation.getArgument(0);
                cb.onError("test failure");
                return null;
            }).when(controller).startMeasurement(any());
            try {
                java.lang.reflect.Field f = MeasureFragment.class.getDeclaredField("measurementController");
                f.setAccessible(true);
                f.set(this, controller);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return view;
        }
    }
}
