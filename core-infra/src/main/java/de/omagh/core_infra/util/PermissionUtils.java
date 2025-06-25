package de.omagh.core_infra.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

/**
 * Utility helpers for runtime permission management.
 */
public class PermissionUtils {

    private PermissionUtils() {
        // no instance
    }

    /**
     * Returns true if the given permission is already granted.
     */
    public static boolean hasPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests a permission with rationale support.
     * <p>
     * If the permission is not granted and a rationale should be shown,
     * a dialog with the provided message will appear. When the user agrees,
     * the launcher will be triggered to request the permission.
     * </p>
     */
    public static void requestPermissionWithRationale(
            Fragment fragment,
            String permission,
            String rationale,
            ActivityResultLauncher<String> launcher) {
        if (hasPermission(fragment.requireContext(), permission)) {
            return;
        }
        if (fragment.shouldShowRequestPermissionRationale(permission)) {
            new AlertDialog.Builder(fragment.requireContext())
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(rationale)
                    .setPositiveButton(android.R.string.ok,
                            (d, w) -> launcher.launch(permission))
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();
        } else {
            launcher.launch(permission);
        }
    }

    /**
     * Shows a simple toast when the user denies a permission.
     */
    public static void showPermissionDenied(Fragment fragment, String message) {
        android.widget.Toast.makeText(fragment.requireContext(), message,
                android.widget.Toast.LENGTH_LONG).show();
    }
}