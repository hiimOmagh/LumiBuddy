package de.omagh.lumibuddy.feature_measurement;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link CalibrationManager} using SharedPreferences.
 */
@RunWith(AndroidJUnit4.class)
public class CalibrationManagerTest {
    private CalibrationManager manager;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("calibration_prefs", Context.MODE_PRIVATE).edit().clear().commit();
        manager = new CalibrationManager(context);
    }

    /**
     * Setting and reading a calibration factor should persist correctly.
     */
    @Test
    public void setAndGetFactor() {
        manager.setCalibrationFactor("L1", 0.05f);
        assertEquals(0.05f, manager.getCalibrationFactor("L1"), 0.0001f);
    }

    /**
     * Unknown IDs should return the default factor.
     */
    @Test
    public void unknownId_returnsDefault() {
        assertEquals(CalibrationManager.DEFAULT_FACTOR, manager.getCalibrationFactor("unknown"), 0.0001f);
    }
}