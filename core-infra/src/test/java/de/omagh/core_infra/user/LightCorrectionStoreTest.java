package de.omagh.core_infra.user;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link LightCorrectionStore}.
 */
public class LightCorrectionStoreTest {
    @Test
    public void storeAndLoadFactor() {
        Context ctx = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = ctx.getSharedPreferences("light_corrections", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
        LightCorrectionStore store = new LightCorrectionStore(ctx);
        store.setFactor("HPS", 1.5f);
        assertEquals(1.5f, store.getFactor("HPS"), 0.0001f);
    }
}