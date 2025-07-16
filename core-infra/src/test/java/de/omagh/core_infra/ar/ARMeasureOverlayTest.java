package de.omagh.core_infra.ar;

import static org.mockito.Mockito.*;

import android.graphics.Canvas;
import android.view.View;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ViewRenderable;

import org.junit.Before;
import org.junit.Test;

import de.omagh.core_domain.model.Measurement;

public class ARMeasureOverlayTest {
    private HeatmapOverlayView overlayView;
    private ArSceneView sceneView;
    private Session session;
    private Pose pose;

    @Before
    public void setup() {
        overlayView = mock(HeatmapOverlayView.class);
        sceneView = mock(ArSceneView.class);
        session = mock(Session.class);
        Frame frame = mock(Frame.class);
        com.google.ar.core.Camera camera = mock(com.google.ar.core.Camera.class);
        pose = mock(Pose.class);
        Anchor anchor = mock(Anchor.class);
        Scene scene = mock(Scene.class);

        when(sceneView.getSession()).thenReturn(session);
        when(sceneView.getArFrame()).thenReturn(frame);
        when(sceneView.getScene()).thenReturn(scene);
        when(frame.getCamera()).thenReturn(camera);
        when(camera.getPose()).thenReturn(pose);
        when(session.createAnchor(pose)).thenReturn(anchor);
    }

    @Test
    public void renderOverlay_createsAnchor() throws Exception {
        ARMeasureOverlay overlay = new ARMeasureOverlay(overlayView) {
            @Override
            protected ArSceneView createSceneView(android.content.Context context) {
                return sceneView;
            }

            @Override
            protected ViewRenderable buildViewRenderable(View view) {
                return mock(ViewRenderable.class);
            }
        };
        overlay.init();
        Measurement m = new Measurement();
        m.ppfd = 100f;
        overlay.renderOverlay(new Canvas(), m, new float[][]{{1f}});
        verify(session).createAnchor(pose);
    }
}