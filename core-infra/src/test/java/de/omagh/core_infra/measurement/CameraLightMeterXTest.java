package de.omagh.core_infra.measurement;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import androidx.camera.core.ImageInfo;

import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.view.PreviewView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Unit tests for {@link CameraLightMeterX} covering start/stop and analysis callbacks.
 */
public class CameraLightMeterXTest {
    @Mock
    Activity activity;
    @Mock
    PreviewView previewView;

    @Mock
    ImageAnalysis imageAnalysis;

    private AutoCloseable mocks;
    private TestMeter meter;

    @Before
    public void setup() throws Exception {
        mocks = MockitoAnnotations.openMocks(this);
        meter = new TestMeter(activity, previewView);
        java.lang.reflect.Field f = CameraLightMeterX.class.getDeclaredField("imageAnalysis");
        f.setAccessible(true);
        f.set(meter, imageAnalysis);
    }

    @After
    public void tearDown() throws Exception {
        meter.stopCamera();
        mocks.close();
    }

    @Test
    public void startStop_createsAndShutsExecutor() throws Exception {
        meter.startCamera();
        ExecutorService exec = meter.getExecutor();
        assertNotNull(exec);
        meter.startCamera(); // second call ignored
        assertSame(exec, meter.getExecutor());
        meter.stopCamera();
        assertTrue(exec.isShutdown());
        assertNull(meter.getExecutor());
    }

    @Test
    public void analyzeFrame_invokesCallback() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        CameraLightMeterX.ResultCallback cb = new CameraLightMeterX.ResultCallback() {
            @Override public void onResult(float r, float g, float b) { latch.countDown(); }
            @Override public void onError(String m) { }
        };
        final ImageAnalysis.Analyzer[] holder = new ImageAnalysis.Analyzer[1];
        doAnswer(inv -> { holder[0] = inv.getArgument(1); return null; })
                .when(imageAnalysis).setAnalyzer(any(), any());
        meter.startCamera();
        meter.analyzeFrame(cb);
        ImageProxy img = createImage();
        holder[0].analyze(img);
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void analyzeFrame_withoutCamera_callsError() {
        java.lang.reflect.Field f;
        try {
            f = CameraLightMeterX.class.getDeclaredField("imageAnalysis");
            f.setAccessible(true);
            f.set(meter, null);
        } catch (Exception e) { throw new RuntimeException(e); }
        CameraLightMeterX.ResultCallback cb = mock(CameraLightMeterX.ResultCallback.class);
        meter.analyzeFrame(cb);
        verify(cb).onError(any());
    }

    // helper image with all zeros
    private ImageProxy createImage() {
        return new ImageProxy() {
            @Override public int getWidth() { return 2; }
            @Override public int getHeight() { return 2; }
            @Override public int getFormat() { return ImageFormat.YUV_420_888; }
            @Override public ImageProxy.PlaneProxy[] getPlanes() {
                ByteBuffer buf = ByteBuffer.allocate(4);
                PlaneProxy plane = new PlaneProxy() {
                    @Override public ByteBuffer getBuffer() { return buf; }
                    @Override public int getPixelStride() { return 1; }
                    @Override public int getRowStride() { return 2; }
                };
                return new PlaneProxy[]{plane, plane, plane};
            }
            @Override public void close() {}
            @Override
            @androidx.camera.core.ExperimentalGetImage
            public Image getImage() { return null; }

            @Override public Rect getCropRect() { return new Rect(); }

            @Override public void setCropRect(Rect rect) { }

            @Override public ImageInfo getImageInfo() {
                return new ImageInfo() {
                    @Override
                    public androidx.camera.core.impl.TagBundle getTagBundle() {
                        return androidx.camera.core.impl.TagBundle.emptyBundle();
                    }

                    @Override public long getTimestamp() { return 0; }

                    @Override public int getRotationDegrees() { return 0; }

                    @Override
                    public Matrix getSensorToBufferTransformMatrix() {
                        return new Matrix();
                    }

                    @Override
                    public void populateExifData(
                            androidx.camera.core.impl.utils.ExifData.Builder exifBuilder) {}
                };
            }
        };
    }

    // subclass exposing executor
    private static class TestMeter extends CameraLightMeterX {
        TestMeter(Activity a, PreviewView p) { super(a, p); }
        ExecutorService getExecutor() throws Exception {
            java.lang.reflect.Field f = CameraLightMeterX.class.getDeclaredField("cameraExecutor");
            f.setAccessible(true);
            return (ExecutorService) f.get(this);
        }
    }
}
