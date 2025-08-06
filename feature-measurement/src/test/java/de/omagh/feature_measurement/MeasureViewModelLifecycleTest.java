package de.omagh.feature_measurement;

import static org.mockito.Mockito.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Method;

import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_infra.calibration.CalibrationRepository;
import de.omagh.shared_ml.LampIdentifier;
import de.omagh.feature_measurement.ui.MeasureViewModel;

/**
 * Verifies that MeasureViewModel releases ML resources on clearing.
 */
public class MeasureViewModelLifecycleTest {
    @Mock CalibrationProfilesManager profileManager;
    @Mock GrowLightProfileManager growLightManager;
    @Mock SettingsManager settingsManager;
    @Mock GetCurrentLuxUseCase getCurrentLuxUseCase;
    @Mock CalibrationManager calibrationManager;
    @Mock SunlightEstimator sunlightEstimator;
    @Mock DiaryRepository diaryRepository;
    @Mock LightCorrectionRepository lightCorrectionStore;
    @Mock CalibrationRepository calibrationRepository;
    @Mock LampIdentifier lampIdentifier;

    private MeasureViewModel viewModel;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        when(calibrationRepository.getDefaultCalibrationFactor()).thenReturn(1f);
        when(settingsManager.getLightDuration()).thenReturn(12);
        when(settingsManager.isAutoSunlightEstimationEnabled()).thenReturn(false);
        when(settingsManager.getSelectedCalibrationProfileId()).thenReturn(null);
        when(growLightManager.getActiveLampProfile())
                .thenReturn(new LampProduct("1", "Lamp", "Brand", "LED", "full", 100, 0.02f, 0f));
        viewModel = new MeasureViewModel(app, profileManager, growLightManager, settingsManager,
                getCurrentLuxUseCase, calibrationManager, sunlightEstimator, diaryRepository,
                lightCorrectionStore, calibrationRepository, lampIdentifier);
    }

    @Test
    public void onCleared_closesIdentifier() throws Exception {
        Method m = MeasureViewModel.class.getDeclaredMethod("onCleared");
        m.setAccessible(true);
        m.invoke(viewModel);
        verify(lampIdentifier).close();
    }
}
