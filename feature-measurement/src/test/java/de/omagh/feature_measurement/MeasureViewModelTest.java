package de.omagh.feature_measurement;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import de.omagh.feature_measurement.ui.MeasureViewModel;

import org.junit.Test;

public class MeasureViewModelTest {
    @Test
    public void defaultValues_areZero() {
        Application app = ApplicationProvider.getApplicationContext();
        MeasureViewModel vm = new MeasureViewModel(app);
        assertNotNull(vm.getLux().getValue());
        assertNotNull(vm.getPPFD().getValue());
        assertEquals(0f, vm.getLux().getValue(), 0.001f);
        assertEquals(0f, vm.getPPFD().getValue(), 0.001f);
    }
}