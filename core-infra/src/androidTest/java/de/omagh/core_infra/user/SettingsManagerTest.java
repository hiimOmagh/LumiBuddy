package de.omagh.core_infra.user;

import android.content.Context;

import de.omagh.core_infra.user.SettingsManager;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Integration tests for {@link SettingsManager}.
 */
@RunWith(AndroidJUnit4.class)
public class SettingsManagerTest {
    private SettingsManager manager;
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE).edit().clear().commit();
        manager = new SettingsManager(context);
    }

    /**
     * Setting and reading preferences should work.
     */
    @Test
    public void setAndGetPreferences() {
        manager.setPreferredUnit("PPFD");
        manager.setLightDuration(12);
        assertEquals("PPFD", manager.getPreferredUnit());
        assertEquals(12, manager.getLightDuration());
    }

    /**
     * Onboarding completion state should persist.
     */
    @Test
    public void onboardingCompletionPersists() {
        assertFalse(manager.isOnboardingComplete());
        manager.setOnboardingComplete(true);
        assertTrue(manager.isOnboardingComplete());
    }
}
