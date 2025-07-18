package de.omagh.feature_measurement;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;

import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.core_infra.environment.SunlightEstimator;

/**
 * Sample unit test for {@link MeasureViewModel#saveMeasurementEntry(String)}.
 */
public class MeasureViewModelSaveEntryTest {
    private DiaryRepository diaryRepository;
    private MeasureViewModel viewModel;

    @Before
    public void setup() {
        Application app = ApplicationProvider.getApplicationContext();
        CalibrationProfilesManager profiles = mock(CalibrationProfilesManager.class);
        GrowLightProfileManager growLightManager = mock(GrowLightProfileManager.class);
        SettingsManager settings = mock(SettingsManager.class);
        GetCurrentLuxUseCase luxUseCase = mock(GetCurrentLuxUseCase.class);
        CalibrationManager calibration = mock(CalibrationManager.class);
        SunlightEstimator estimator = mock(SunlightEstimator.class);
        diaryRepository = mock(DiaryRepository.class);
        LightCorrectionRepository correction = mock(LightCorrectionRepository.class);

        when(settings.getLightDuration()).thenReturn(12);
        when(settings.getSelectedCalibrationProfileId()).thenReturn("lamp1");
        LampProduct lamp = new LampProduct("lamp1", "Test", "", "LED", "full", 100, 1f, 0f);
        when(growLightManager.getActiveLampProfile()).thenReturn(lamp);
        when(growLightManager.getById("lamp1")).thenReturn(lamp);
        when(profiles.getCalibrationFactorForSource(any())).thenReturn(1f);
        when(correction.getFactor(any())).thenReturn(1f);

        viewModel = new MeasureViewModel(app, profiles, growLightManager, settings,
                luxUseCase, calibration, estimator, diaryRepository, correction);
        viewModel.setLux(100f, "ALS");
    }

    @Test
    public void saveMeasurementEntry_insertsDiaryRecord() {
        viewModel.saveMeasurementEntry("p1");
        verify(diaryRepository).insert(any(de.omagh.core_data.model.DiaryEntry.class));
    }
}
