# Manual QA Guide

This checklist outlines the key device-based validation steps for LumiBuddy. Run through these tasks on a physical device before releases.

## Calibration UI

1. Launch the **Calibration Wizard** from Settings.
2. Capture a reading with the light sensor or camera.
3. Enter the reference PPFD value and finish the wizard.
4. Re-open the measurement screen and confirm the factor is applied.

## AR Overlays

1. Open the AR measurement overlay from the debug menu.
2. Grant the camera permission when prompted.
3. Move the device until a plane is detected and tap to place the marker.
4. Verify the overlay updates its lux readout and color as light changes.

## ML Recognition

1. From the **Add Plant** screen choose *Identify Plant*.
2. Point the camera at a plant and wait for a prediction.
3. Confirm the predicted species appears in the UI.

## Navigation Flows

- Use the bottom navigation to switch between Home, Diary, Measure and Plant DB screens.
- Opening an item from the grow schedule should deep link to its detail screen.
- The system back button should return to the previous fragment without exiting unexpectedly.

## Permission Handling

- First use of camera, storage or notifications should request permissions.
- If a permission is denied a rationale is shown and the feature remains disabled until granted.

## General Stability

- Rotate the device and ensure each screen retains state when returning from the rotated orientation.
- Put the app in the background and then bring it to the foreground to verify it restores to the last screen.
- Deny a previously granted permission and re-enable it to confirm the feature recovers without a restart.
- Watch logcat or LeakCanary for signs of memory leaks or crashes during extended use.
Following this checklist helps catch regressions before release.
