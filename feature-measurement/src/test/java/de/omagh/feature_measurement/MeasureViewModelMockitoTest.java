package de.omagh.feature_measurement;

import static org.junit.Assert.assertEquals;

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
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_infra.calibration.CalibrationRepository;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.shared_ml.LampIdentifier;

/**
 * Tests {@link MeasureViewModel#setLux(float, String)} using mocked dependencies.
 */
public class MeasureViewModelMockitoTest {
    @Mock
    GrowLightProfileManager growLightManager;
    @Mock
    CalibrationProfilesManager profileManager;
    @Mock
    SettingsManager settings;
    @Mock
    GetCurrentLuxUseCase getCurrentLuxUseCase;
    @Mock
    CalibrationManager calibrationManager;
    @Mock
    SunlightEstimator sunlightEstimator;
    @Mock
    DiaryRepository diaryRepository;
    @Mock
    LightCorrectionRepository lightCorrectionStore;
    @Mock
    CalibrationRepository calibrationRepository;
    @Mock
    LampIdentifier lampIdentifier;
    
    private MeasureViewModel vm;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
         Mockito.when(calibrationRepository.getDefaultCalibrationFactor()).thenReturn(1f);
        Mockito.when(settings.getLightDuration()).thenReturn(12);
        Mockito.when(settings.isAutoSunlightEstimationEnabled()).thenReturn(false);
        Mockito.when(settings.getSelectedCalibrationProfileId()).thenReturn("lamp1");
        Mockito.when(lightCorrectionStore.getFactor(Mockito.anyString())).thenReturn(1f);
        Mockito.when(growLightManager.getById("lamp1"))
                .thenReturn(new LampProduct("lamp1", "Lamp", "Brand", "LED", "full", 100, 0.02f, 0f));
        Mockito.when(profileManager.getCalibrationFactorForSource(Mockito.anyString()))
                .thenReturn(1f);

        vm = new MeasureViewModel(app, profileManager, growLightManager, settings,
                getCurrentLuxUseCase, calibrationManager, sunlightEstimator, diaryRepository,
                lightCorrectionStore, calibrationRepository, lampIdentifier);
    }

    @Test
public void setLux_alsSourceUsesCorrectFactors() {
        Mockito.when(profileManager.getCalibrationFactorForSource("ALS")).thenReturn(1.2f);
        Mockito.when(lightCorrectionStore.getFactor("LED")).thenReturn(1.25f);

    vm.setLux(100f, "ALS");

     float lampFactor = 0.02f * 1.25f;
        float deviceFactor = 1.2f;
        float expected = lampFactor * deviceFactor;
        assertEquals(expected, vm.getCalibrationFactor().getValue(), 0.0001f);
        assertEquals(100f * expected, vm.getPPFD().getValue(), 0.001f);
        Mockito.verify(profileManager).getCalibrationFactorForSource("ALS");
        Mockito.verify(lightCorrectionStore).getFactor("LED");
    }

    @Test
    public void setLux_cameraSourceUsesDeviceFactor() {
        Mockito.when(profileManager.getCalibrationFactorForSource("CAM")).thenReturn(1.5f);

        vm.setLux(100f, "CAM");

        float lampFactor = 0.02f; // light correction factor default 1
        float deviceFactor = 1.5f;
        float expected = lampFactor * deviceFactor;
        assertEquals(expected, vm.getCalibrationFactor().getValue(), 0.0001f);
        assertEquals(100f * expected, vm.getPPFD().getValue(), 0.001f);
        Mockito.verify(profileManager).getCalibrationFactorForSource("CAM");
        Mockito.verify(lightCorrectionStore).getFactor("LED");        
    }
}
