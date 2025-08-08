package de.omagh.shared_arcore;

import android.app.Activity;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

/**
 * Default implementation that creates real {@link Session} instances when ARCore is available.
 *
 * <p>The implementation performs capability checks and propagates informative exceptions in
 * situations where a session cannot be created.</p>
 */
public class DefaultArCoreSessionProvider implements ArCoreSessionProvider {

    @Override
    public Session createSession(Activity activity) {
        if (!isArCoreSupported(activity)) {
            throw new UnsupportedOperationException("ARCore is not supported on this device");
        }

        try {
            // Ensure ARCore is installed and up to date.
            ArCoreApk.InstallStatus status =
                    ArCoreApk.getInstance().requestInstall(activity, /* userRequestedInstall= */ true);
            if (status != ArCoreApk.InstallStatus.INSTALLED) {
                throw new IllegalStateException("ARCore installation not completed");
            }

            return new Session(activity);
        } catch (UnavailableArcoreNotInstalledException e) {
            throw new IllegalStateException("ARCore is not installed", e);
        } catch (UnavailableUserDeclinedInstallationException e) {
            throw new IllegalStateException("User declined installation of ARCore", e);
        } catch (UnavailableApkTooOldException e) {
            throw new IllegalStateException("ARCore APK too old", e);
        } catch (UnavailableSdkTooOldException e) {
            throw new IllegalStateException("ARCore SDK too old", e);
        } catch (UnavailableDeviceNotCompatibleException e) {
            throw new UnsupportedOperationException("Device incompatible with ARCore", e);
        }
    }

    @Override
    public boolean isArCoreSupported(Activity activity) {
        return ArCoreApk.getInstance().checkAvailability(activity).isSupported();
    }
}
