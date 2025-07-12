package de.omagh.core_infra.ar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import de.omagh.core_infra.ar.HeatmapOverlayView;

import de.omagh.core_domain.model.Measurement;

import com.google.ar.core.Anchor;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import timber.log.Timber;

/**
 * Stub implementation of {@link AROverlayRenderer}.
 * This class does not perform any real AR rendering yet. It simply logs
 * the calls it receives and acts as an integration point for a future
 * ARCore based overlay renderer.
 */
public class ARMeasureOverlay implements AROverlayRenderer {

    private static final String TAG = "ARMeasureOverlay";
    private final HeatmapOverlayView overlayView;
    private ArSceneView sceneView;
    private Anchor anchor;
    private AnchorNode anchorNode;
    private ViewRenderable viewRenderable;
    public ARMeasureOverlay(HeatmapOverlayView view) {
        this.overlayView = view;
    }

    @Override
    public void init() {
        Timber.tag(TAG).d("init() called");
        if (overlayView != null) {
            overlayView.setVisibility(View.VISIBLE);
        }
        try {
            sceneView = new ArSceneView(overlayView.getContext());
            sceneView.resume();
        } catch (Exception e) {
            Timber.tag(TAG).e(e, "Failed to initialize ArSceneView");
        }
    }

    @Override
    public void renderOverlay(Canvas canvas, Measurement measurement, float[][] intensityGrid) {
        Timber.tag(TAG).d("renderOverlay() called with measurement=%s", measurement);
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
                    anchor = session.createAnchor(session.getCamera().getPose());
                    if (anchorNode != null) {
                        anchorNode.setParent(null);
                    }
                    anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(sceneView.getScene());

                    TextView tv = new TextView(overlayView.getContext());
                    tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                    tv.setText(String.format(java.util.Locale.US, "%.1f", measurement.ppfd));

                    try {
                        viewRenderable = ViewRenderable.builder()
                                .setView(overlayView.getContext(), tv)
                                .build()
                                .get();
                    } catch (Exception e) {
                        Timber.tag(TAG).e(e, "Failed to build ViewRenderable");
                    }

                    if (viewRenderable != null) {
                        Node node = new Node();
                        node.setParent(anchorNode);
                        node.setRenderable(viewRenderable);
                    }
                } catch (Exception e) {
                    Timber.tag(TAG).e(e, "Error creating AR anchor");
                }
            }
        }
    }

    @Override
    public void cleanup() {
        Timber.tag(TAG).d("cleanup() called");
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