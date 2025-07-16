package de.omagh.feature_measurement;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.LightCorrectionStore;
import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_data.repository.DiaryRepository;

import org.mockito.Mockito;

import org.junit.Test;

public class MeasureViewModelTest {
    @Test
    public void defaultValues_areZero() {
        Application app = ApplicationProvider.getApplicationContext();
        CalibrationProfilesManager pm = Mockito.mock(CalibrationProfilesManager.class);
        GrowLightProfileManager gm = Mockito.mock(GrowLightProfileManager.class);
        SettingsManager sm = Mockito.mock(SettingsManager.class);
        GetCurrentLuxUseCase useCase = Mockito.mock(GetCurrentLuxUseCase.class);
        CalibrationManager cm = Mockito.mock(CalibrationManager.class);
        SunlightEstimator estimator = Mockito.mock(SunlightEstimator.class);
        DiaryRepository diaryRepository = Mockito.mock(DiaryRepository.class);
        LightCorrectionStore store = Mockito.mock(LightCorrectionStore.class);
        MeasureViewModel vm = new MeasureViewModel(app, pm, gm, sm, useCase, cm,
                estimator, diaryRepository, store);
        assertNotNull(vm.getLux().getValue());
        assertNotNull(vm.getPPFD().getValue());
        assertEquals(0f, vm.getLux().getValue(), 0.001f);
        assertEquals(0f, vm.getPPFD().getValue(), 0.001f);
    }
}
