package de.omagh.shared_arcore;

import android.app.Activity;
import com.google.ar.core.Session;

/**
 * Placeholder implementation used on devices without ARCore.
 */
public class NoOpArCoreSessionProvider implements ArCoreSessionProvider {
    @Override
    public Session createSession(Activity activity) {
        throw new UnsupportedOperationException("ARCore not available");
    }

    @Override
    public boolean isArCoreSupported(Activity activity) {
        return false;
    }
}
