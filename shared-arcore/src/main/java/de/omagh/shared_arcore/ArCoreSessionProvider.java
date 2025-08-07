package de.omagh.shared_arcore;

import android.app.Activity;
import com.google.ar.core.Session;

/**
 * Abstraction layer for creating ARCore sessions.
 */
public interface ArCoreSessionProvider {
    /**
     * Creates a new {@link Session} or throws if ARCore is unavailable.
     *
     * @param activity hosting activity
     * @return a configured ARCore {@link Session}
     */
    Session createSession(Activity activity);

    /**
     * Checks whether ARCore is supported on the current device.
     *
     * @param activity hosting activity
     * @return true if ARCore is available
     */
    boolean isArCoreSupported(Activity activity);
}
