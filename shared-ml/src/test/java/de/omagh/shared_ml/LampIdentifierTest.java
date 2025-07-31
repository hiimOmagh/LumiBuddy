package de.omagh.shared_ml;

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

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.tensorflow.lite.Interpreter;

/**
 * Unit tests for {@link LampIdentifier} verifying model loading, inference and cleanup.
 */
public class LampIdentifierTest {
    @Mock
    ModelProvider provider;

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
    public void identifyLamp_returnsPredictionAboveThreshold() throws Exception {
        ByteBuffer model = ByteBuffer.allocate(4);
        when(provider.loadModel(context)).thenReturn(model);
        try (MockedConstruction<Interpreter> construction = Mockito.mockConstruction(Interpreter.class)) {
            LampIdentifier id = new LampIdentifier(context, provider, 0.2f);
            Bitmap bmp = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
            Interpreter interpreter = construction.constructed().get(0);
            doAnswer(inv -> {
                float[][] out = (float[][]) inv.getArguments()[1];
                out[0][1] = 0.9f;
                return null;
            }).when(interpreter).run(any(), any());
            CountDownLatch latch = new CountDownLatch(1);
            id.identifyLamp(bmp).observeForever(p -> {
                assertNotNull(p);
                assertFalse(p.isEmpty());
                assertEquals("Lamp", p.get(0).getLabel());
                latch.countDown();
            });
            assertTrue(latch.await(1, TimeUnit.SECONDS));
            id.close();
        }
    }

    @Test
    public void close_shutsExecutor() throws Exception {
        ByteBuffer model = ByteBuffer.allocate(4);
        when(provider.loadModel(context)).thenReturn(model);
        try (MockedConstruction<Interpreter> construction = Mockito.mockConstruction(Interpreter.class)) {
            LampIdentifier id = new LampIdentifier(context, provider);
            Interpreter interp = construction.constructed().get(0);
            id.close();
            verify(interp).close();
            java.lang.reflect.Field f = LampIdentifier.class.getDeclaredField("executor");
            f.setAccessible(true);
            java.util.concurrent.ExecutorService exec = (java.util.concurrent.ExecutorService) f.get(id);
            assertTrue(exec.isShutdown());
        }
    }
}
