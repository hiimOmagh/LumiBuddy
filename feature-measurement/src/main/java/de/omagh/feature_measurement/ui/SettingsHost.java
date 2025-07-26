package de.omagh.feature_measurement.ui;

/**
 * Host callback for {@link SettingsFragment} actions that require app level navigation.
 */
public interface SettingsHost {
    /**
     * Called when the user requests to view the privacy policy from the settings screen.
     */
    void onShowPrivacyPolicy();
}
