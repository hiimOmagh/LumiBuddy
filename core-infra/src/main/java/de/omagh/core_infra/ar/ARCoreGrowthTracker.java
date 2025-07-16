package de.omagh.core_infra.ar;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Basic implementation of {@link ARGrowthTracker} using ARCore. It places a
 * marker at the current camera pose whenever {@link #trackGrowth(Bitmap)} is
 * called. The passed bitmap is ignored because the frame from the AR scene view
 * is used directly.
 */
public class ARCoreGrowthTracker implements ARGrowthTracker {

    private static final String TAG = "ARCoreGrowthTracker";

    private final ArFragment arFragment;
    private final List<AnchorNode> placedAnchors = new ArrayList<>();
    private Session session;

    public ARCoreGrowthTracker(ArFragment fragment) {
        this.arFragment = fragment;
    }

    @Override
    public void init() {
        if (arFragment == null) return;
        Context ctx = arFragment.getContext();
        if (ctx == null) return;
        try {
            session = new Session(ctx);
            arFragment.getArSceneView().setupSession(session);
            arFragment.getArSceneView().resume();
            Timber.tag(TAG).d("ARCore session initialized");
        } catch (Exception e) {
            Timber.tag(TAG).e(e, "Failed to init ARCore session");
        }
    }

    @Override
    public void trackGrowth(Bitmap currentView) {
        if (arFragment == null || session == null) return;
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) return;
        try {
            Anchor anchor = session.createAnchor(frame.getCamera().getPose());
            AnchorNode node = new AnchorNode(anchor);
            node.setParent(arFragment.getArSceneView().getScene());
            placedAnchors.add(node);
        } catch (Exception e) {
            Timber.tag(TAG).e(e, "Failed to place AR marker");
        }
    }

    @Override
    public void cleanup() {
        for (AnchorNode node : placedAnchors) {
            try {
                Anchor anchor = node.getAnchor();
                if (anchor != null) {
                    anchor.detach();
                }
                node.setParent(null);
            } catch (Exception ignored) {
            }
        }
        placedAnchors.clear();
        if (arFragment != null && arFragment.getArSceneView() != null) {
            try {
                arFragment.getArSceneView().pause();
            } catch (Exception ignored) {
            }
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception ignored) {
            }
            session = null;
        }
    }
}
