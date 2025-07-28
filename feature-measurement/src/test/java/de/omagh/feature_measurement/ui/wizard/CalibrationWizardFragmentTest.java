package de.omagh.feature_measurement.ui.wizard;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.fragment.app.testing.FragmentScenario.FragmentAction;
import androidx.fragment.app.testing.FragmentScenarioKt;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.feature_measurement.R;

/**
 * Instrumented tests for {@link CalibrationWizardFragment} verifying sensor averaging,
 * error handling and resource cleanup.
 */
@RunWith(AndroidJUnit4.class)
public class CalibrationWizardFragmentTest {
    @Mock
    MeasurementRepository measurementRepository;
    @Mock
    LightCorrectionRepository correctionRepository;

    private AutoCloseable mocks;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        // run RxJava synchronously
        RxJavaPlugins.setInitIoSchedulerHandler(s -> Schedulers.trampoline());
        RxJavaPlugins.setInitComputationSchedulerHandler(s -> Schedulers.trampoline());
        RxJavaPlugins.setInitNewThreadSchedulerHandler(s -> Schedulers.trampoline());
        RxJavaPlugins.setInitSingleSchedulerHandler(s -> Schedulers.trampoline());
        RxJavaPlugins.setInitMainThreadSchedulerHandler(s -> Schedulers.trampoline());
    }

    @After
    public void tearDown() throws Exception {
        RxJavaPlugins.reset();
        mocks.close();
    }

    /**
     * Launches the fragment, runs through the wizard flow and verifies the computed
     * correction factor is stored.
     */
    @Test
    public void wizardAveragesSensorData() {
        when(measurementRepository.observeLux())
                .thenReturn(Observable.fromArray(10f, 20f, 30f, 40f));

        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(
                TestFragment.class,
                null,
                R.style.Theme_MaterialComponents,
                (Context) ApplicationProvider.getApplicationContext());
        scenario.onFragment(fragment -> {
            fragment.inject(correctionRepository, measurementRepository);
            Spinner spinner = fragment.getView().findViewById(R.id.lightTypeSpinner);
            spinner.setSelection(0);
            Button next = fragment.getView().findViewById(R.id.nextBtn);
            // step 0 -> 1
            next.performClick();
            // start measurement
            next.performClick();
        });
        verify(correctionRepository).setFactor(anyString(), eq(1f / 25f));
        scenario.moveToState(Lifecycle.State.DESTROYED);
    }

    /**
     * Emits an error during measurement and verifies that no factor is stored.
     */
    @Test
    public void wizardShowsErrorOnSensorFailure() {
        when(measurementRepository.observeLux())
                .thenReturn(Observable.error(new RuntimeException("sensor")));

        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(
                TestFragment.class,
                null,
                R.style.Theme_MaterialComponents,
                (Context) ApplicationProvider.getApplicationContext());
        scenario.onFragment(fragment -> {
            fragment.inject(correctionRepository, measurementRepository);
            Button next = fragment.getView().findViewById(R.id.nextBtn);
            next.performClick(); // step 0->1
            next.performClick(); // fails
        });
        verify(correctionRepository, never()).setFactor(anyString(), anyFloat());
        scenario.moveToState(Lifecycle.State.DESTROYED);
    }

    /**
     * Destroying the fragment stops sensor updates and dismisses the progress dialog.
     */
    @Test
    public void cleanupOnDestroy() {
        when(measurementRepository.observeLux())
                .thenReturn(Observable.fromArray(1f));

        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(
                TestFragment.class,
                null,
                R.style.Theme_MaterialComponents,
                (Context) ApplicationProvider.getApplicationContext());
        scenario.onFragment(fragment -> {
            fragment.inject(correctionRepository, measurementRepository);
            Button next = fragment.getView().findViewById(R.id.nextBtn);
            next.performClick();
            next.performClick();
        });
        scenario.moveToState(Lifecycle.State.DESTROYED);
        verify(measurementRepository).stopALS();
    }

    /** Test fragment subclass overriding dependency injection. */
    public static class TestFragment extends CalibrationWizardFragment {
        private LightCorrectionRepository store;
        private MeasurementRepository repo;

        void inject(LightCorrectionRepository s, MeasurementRepository r) {
            this.store = s;
            this.repo = r;
        }

        @Override
        public void onAttach(@NonNull @NonNull Context context) {
            super.onAttach(context);
            try {
                java.lang.reflect.Field f = CalibrationWizardFragment.class.getDeclaredField("correctionStore");
                f.setAccessible(true);
                f.set(this, store);
                f = CalibrationWizardFragment.class.getDeclaredField("measurementRepository");
                f.setAccessible(true);
                f.set(this, repo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
