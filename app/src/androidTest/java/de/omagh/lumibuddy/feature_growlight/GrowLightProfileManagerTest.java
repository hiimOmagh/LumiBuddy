package de.omagh.lumibuddy.feature_growlight;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link GrowLightProfileManager} using real SharedPreferences.
 */
@RunWith(AndroidJUnit4.class)
public class GrowLightProfileManagerTest {
    private Context context;
    private GrowLightProfileManager manager;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("grow_light_profiles", Context.MODE_PRIVATE).edit().clear().commit();
        manager = new GrowLightProfileManager(context);
    }

    /**
     * Adding a custom profile should be persisted and retrievable.
     */
    @Test
    public void addAndRetrieveCustomProfile() {
        LampProduct lamp = new LampProduct("ID1", "Test", "Brand", "LED", "full", 10, 0.02f, 100);
        manager.addCustomProfile(lamp);
        List<LampProduct> all = manager.getAllProfiles();
        assertTrue(all.contains(lamp));
    }

    /**
     * Updating a profile should change the stored entry.
     */
    @Test
    public void updateProfile_changesEntry() {
        LampProduct lamp = new LampProduct("ID2", "Test", "Brand", "LED", "full", 10, 0.02f, 100);
        manager.addCustomProfile(lamp);
        lamp.name = "Updated";
        manager.updateProfile(lamp);
        LampProduct result = manager.getById("ID2");
        assertEquals("Updated", result.name);
    }

    /**
     * Removing a profile should delete it from storage.
     */
    @Test
    public void removeProfile_deletesEntry() {
        LampProduct lamp = new LampProduct("ID3", "Test", "Brand", "LED", "full", 10, 0.02f, 100);
        manager.addCustomProfile(lamp);
        manager.removeCustomProfile("ID3");
        assertNull(manager.getById("ID3"));
    }
}