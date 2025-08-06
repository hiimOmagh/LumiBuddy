package de.omagh.core_infra.ml;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.tensorflow.lite.Interpreter;

/**
 * Unit tests for {@link OnDevicePlantClassifier} verifying classification logic and cleanup.
 */
public class OnDevicePlantClassifierTest {
    @Mock
    Interpreter interpreter;

    private AutoCloseable mocks;
    private Context context;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void classify_updatesLastResult() throws Exception {
        InputStream is = new ByteArrayInputStream(new byte[4]);
        Context spy = spy(context);
        doReturn(is).when(spy).getAssets().open(anyString());
        try (MockedConstruction<Interpreter> cons = Mockito.mockConstruction(Interpreter.class, (m, c) -> {
        })) {
            OnDevicePlantClassifier c = new OnDevicePlantClassifier(spy);
            Interpreter intr = cons.constructed().get(0);
            doAnswer(inv -> {
                float[][] out = (float[][]) inv.getArguments()[1];
                out[0][0] = 0.6f;
                return null;
            }).when(intr).run(any(), any());
            c.classify(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            assertEquals("Known", c.getLastResult());
            c.close();
            verify(intr).close();
        }
    }
}
