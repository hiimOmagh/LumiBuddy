package de.omagh.feature_measurement;

import static org.junit.Assert.assertEquals;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;

import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.LightCorrectionStore;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_data.repository.DiaryRepository;

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
    LightCorrectionStore lightCorrectionStore;

    private MeasureViewModel vm;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        vm = new MeasureViewModel(app, profileManager, growLightManager, settings,
                getCurrentLuxUseCase, calibrationManager, sunlightEstimator, diaryRepository,
                lightCorrectionStore);

        Field lampField = MeasureViewModel.class.getDeclaredField("lampIdLiveData");
        lampField.setAccessible(true);
        lampField.set(vm, new MutableLiveData<>("lamp1"));

        Mockito.when(growLightManager.getById("lamp1"))
                .thenReturn(new LampProduct("lamp1", "Lamp", "Brand", "LED", "full", 100, 0.02f, 0f));
        Mockito.when(profileManager.getCalibrationFactorForSource(Mockito.anyString()))
                .thenReturn(1f);
    }

    @Test
    public void setLux_updatesPPFD() {
        vm.setLux(100f, "ALS");
        assertEquals(2f, vm.getPPFD().getValue(), 0.001f);
    }
}