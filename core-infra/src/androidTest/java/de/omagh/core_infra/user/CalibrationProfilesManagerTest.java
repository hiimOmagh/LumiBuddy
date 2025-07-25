package de.omagh.core_infra.user;

import android.content.Context;

import de.omagh.core_infra.user.CalibrationProfilesManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.core_domain.model.CalibrationProfile;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link CalibrationProfilesManager}.
 */
@RunWith(AndroidJUnit4.class)
public class CalibrationProfilesManagerTest {
    private CalibrationProfilesManager manager;

    @Before
    public void setup() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("calibration_profiles", Context.MODE_PRIVATE).edit().clear().commit();
        manager = new CalibrationProfilesManager(context);
    }

    /**
     * Adding and selecting a profile should be reflected in getters.
     */
    @Test
    public void addSelectProfile() {
        CalibrationProfile p = new CalibrationProfile("1", "Test", "ALS", 1.5f, "");
        manager.addProfile(p);
        List<CalibrationProfile> list = manager.getProfiles();
        assertTrue(list.contains(p));
        manager.setActiveProfile("1");
        assertEquals(p, manager.getActiveProfile());
    }
}