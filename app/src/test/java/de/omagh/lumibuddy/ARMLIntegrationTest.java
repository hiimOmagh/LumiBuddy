package de.omagh.lumibuddy;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import de.omagh.core_infra.ml.BasicHealthStatusClassifier;
import de.omagh.core_infra.ml.BasicLampTypeClassifier;
import de.omagh.core_infra.ml.BasicPlantClassifier;
import de.omagh.core_infra.ml.HealthStatusClassifier;
import de.omagh.core_infra.ml.LampTypeClassifier;
import de.omagh.core_infra.ml.PlantClassifier;
import de.omagh.core_infra.ar.DummyARGrowthTracker;

public class ARMLIntegrationTest {

    @Test
    public void plantClassifierReturnsUnknown() {
        PlantClassifier c = new BasicPlantClassifier();
        c.classify(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        assertEquals("Unknown", c.getLastResult());
    }

    @Test
    public void lampClassifierReturnsUnknown() {
        LampTypeClassifier c = new BasicLampTypeClassifier();
        Bitmap b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        b.eraseColor(Color.WHITE);
        c.classify(b);
        assertEquals("Unknown Lamp", c.getLastResult());
    }

    @Test
    public void healthClassifierReturnsHealthy() {
        HealthStatusClassifier c = new BasicHealthStatusClassifier();
        c.classify(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        assertEquals("Healthy", c.getLastResult());
    }

    @Test
    public void dummyArGrowthTrackerDoesNotCrash() {
        DummyARGrowthTracker t = new DummyARGrowthTracker();
        t.init();
        t.trackGrowth(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        t.cleanup();
    }
}