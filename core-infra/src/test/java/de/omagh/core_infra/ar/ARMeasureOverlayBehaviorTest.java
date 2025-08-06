package de.omagh.core_infra.ar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.omagh.core_domain.model.Measurement;

/**
 * Additional tests for {@link ARMeasureOverlay} covering error handling and cleanup.
 */
public class ARMeasureOverlayBehaviorTest {
    @Mock
    HeatmapOverlayView overlayView;
    @Mock
    ArSceneView sceneView;
    @Mock
    Session session;
    @Mock
    Frame frame;
    @Mock
    Anchor anchor;
    @Mock
    AnchorNode anchorNode;
    @Mock
    ViewRenderable renderable;
    @Mock
    Scene scene;

    private AutoCloseable mocks;
    private ARMeasureOverlay overlay;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
        when(sceneView.getSession()).thenReturn(session);
        when(sceneView.getScene()).thenReturn(scene);
        when(sceneView.getArFrame()).thenReturn(frame);
        when(session.createAnchor(any(Pose.class))).thenReturn(anchor);
        overlay = new ARMeasureOverlay(overlayView) {
            @Override
            protected ArSceneView createSceneView(Context c) {
                return sceneView;
            }

            @Override
            protected ViewRenderable buildViewRenderable(android.view.View v) {
                return renderable;
            }
        };
    }

    @After
    public void tearDown() throws Exception {
        overlay.cleanup();
        mocks.close();
    }

    @Test
    public void init_handlesFailure() throws Exception {
        doThrow(new RuntimeException("fail"))
                .when(sceneView).resume();
        overlay.init();
        verify(overlayView).setVisibility(android.view.View.VISIBLE);
    }

    @Test
    public void cleanup_resetsOverlay() {
        overlay.init();
        Measurement m = new Measurement(10f, 0f, 0f, 0L, "test");
        overlay.renderOverlay(new Canvas(), m, new float[][]{{1f}});
        overlay.cleanup();
        verify(anchor).detach();
        verify(sceneView).destroy();
    }
}
