package de.omagh.shared_arcore;

import android.app.Activity;
import com.google.ar.core.Session;

/**
 * No-op implementation used on devices that definitively do not support ARCore.
 */
public class NoOpArCoreSessionProvider implements ArCoreSessionProvider {
    @Override
    public Session createSession(Activity activity) {
        throw new UnsupportedOperationException("ARCore is not supported on this device");
    }

    @Override
    public boolean isArCoreSupported(Activity activity) {
        return false;
    }
}
