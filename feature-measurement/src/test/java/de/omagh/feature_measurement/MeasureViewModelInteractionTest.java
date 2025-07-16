package de.omagh.feature_measurement;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_data.repository.DiaryRepository;
import io.reactivex.rxjava3.core.Observable;

/**
 * Tests interaction of {@link MeasureViewModel} with its collaborators.
 */
public class MeasureViewModelInteractionTest {
    @Mock
    GrowLightProfileManager growLightManager;
    @Mock
    CalibrationProfilesManager profileManager;
    @Mock
    SettingsManager settingsManager;
    @Mock
    GetCurrentLuxUseCase luxUseCase;
    @Mock
    CalibrationManager calibrationManager;
    @Mock
    SunlightEstimator sunlightEstimator;
    @Mock
    DiaryRepository diaryRepository;

    private MeasureViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(settingsManager.getLightDuration()).thenReturn(12);
        Mockito.when(settingsManager.getSelectedCalibrationProfileId()).thenReturn("lamp1");
        Mockito.when(growLightManager.getActiveLampProfile()).thenReturn(new de.omagh.core_infra.measurement.LampProduct("lamp1", "", "", "", "", 0, 1f, 0f));
        vm = new MeasureViewModel(app, profileManager, growLightManager, settingsManager,
                luxUseCase, calibrationManager, sunlightEstimator, diaryRepository);
    }

    @Test
    public void startMeasuring_invokesUseCase() {
        Mockito.when(luxUseCase.execute()).thenReturn(Observable.never());
        vm.startMeasuring();
        Mockito.verify(luxUseCase).execute();
    }

    @Test
    public void stopMeasuring_invokesStop() {
        vm.stopMeasuring();
        Mockito.verify(luxUseCase).stop();
    }

    @Test
    public void setLampProfileId_updatesManagers() {
        vm.setLampProfileId("id1");
        Mockito.verify(growLightManager).setActiveLampProfile("id1");
        Mockito.verify(settingsManager).setSelectedCalibrationProfileId("id1");
    }
}
