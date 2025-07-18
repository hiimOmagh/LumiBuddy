package de.omagh.core_infra.util;

import android.Manifest;
import android.content.Context;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

/**
 * Helper for handling POST_NOTIFICATIONS permission across the app.
 */
public class NotificationPermissionHelper {

    private NotificationPermissionHelper() {}

    /**
     * Returns true if the notification permission is granted or not required.
     */
    public static boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return PermissionUtils.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS);
    }

    /**
     * Requests the POST_NOTIFICATIONS permission with rationale if needed.
     */
    public static void requestPermissionIfNeeded(
            Fragment fragment,
            String rationale,
            ActivityResultLauncher<String> launcher) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !hasPermission(fragment.requireContext())) {
            PermissionUtils.requestPermissionWithRationale(
                    fragment,
                    Manifest.permission.POST_NOTIFICATIONS,
                    rationale,
                    launcher);
        }
    }
}
