package de.omagh.core_infra.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import de.omagh.core_infra.ar.HeatmapOverlayView;

import de.omagh.core_domain.model.Measurement;

import com.google.ar.core.Anchor;
import com.google.ar.core.Session;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;

/**
 * Simple implementation of {@link AROverlayRenderer} using Sceneform.
 * An anchor is placed at the current camera pose and a {@link ViewRenderable}
 * displaying the latest measurement is attached to it.
 */
public class ARMeasureOverlay implements AROverlayRenderer {
    private final HeatmapOverlayView overlayView;
    private ArSceneView sceneView;
    private Anchor anchor;
    private AnchorNode anchorNode;
    private ViewRenderable viewRenderable;
    public ARMeasureOverlay(HeatmapOverlayView view) {
        this.overlayView = view;
    }

    /**
     * Factory method used to obtain the {@link ArSceneView}. Overridden in tests
     * to provide a mocked instance.
     */
    protected ArSceneView createSceneView(Context context) {
        return new ArSceneView(context);
    }

    /**
     * Builds the {@link ViewRenderable} for display.
     */
    protected ViewRenderable buildViewRenderable(View view) throws Exception {
        return ViewRenderable.builder()
                .setView(overlayView.getContext(), view)
                .build()
                .get();
    }

    @Override
    public void init() {
        if (overlayView != null) {
            overlayView.setVisibility(View.VISIBLE);
        }
        try {
            assert overlayView != null;
            sceneView = createSceneView(overlayView.getContext());
            sceneView.resume();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement, float[][] intensityGrid) {
        if (overlayView != null && intensityGrid != null) {
            overlayView.setIntensityGrid(intensityGrid);
        }

        if (sceneView != null && measurement != null) {
            Session session = sceneView.getSession();
            if (session != null) {
                try {
                    if (anchor != null) {
                        anchor.detach();
                        anchor = null;
                    }
                    Frame frame = sceneView.getArFrame();
                    if (frame != null) {
                        anchor = session.createAnchor(frame.getCamera().getPose());
                    }
                    if (anchorNode != null) {
                        anchorNode.setParent(null);
                    }
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(sceneView.getScene());

                    assert overlayView != null;
                    TextView tv = new TextView(overlayView.getContext());
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                    tv.setText(String.format(java.util.Locale.US, "%.1f", measurement.ppfd));

                    try {
                        viewRenderable = buildViewRenderable(tv);
                    } catch (Exception ignored) {
                    }

                    if (viewRenderable != null) {
                        Node node = new Node();
                        node.setParent(anchorNode);
                        node.setRenderable(viewRenderable);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    @Override
    public void cleanup() {
        if (overlayView != null) {
            overlayView.setVisibility(View.GONE);
        }
        if (anchorNode != null) {
            anchorNode.setParent(null);
            anchorNode = null;
        }
        if (anchor != null) {
            anchor.detach();
            anchor = null;
        }
        if (sceneView != null) {
            sceneView.pause();
            sceneView.destroy();
            sceneView = null;
        }
    }

}
