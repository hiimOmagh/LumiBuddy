package de.omagh.lumibuddy.feature_measurement;

import android.Manifest;
import android.app.Activity;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.DenyPermissionRule;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.core_infra.user.SettingsManager;
import de.omagh.lumibuddy.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class MeasurePermissionTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public DenyPermissionRule denyCamera = DenyPermissionRule.deny(Manifest.permission.CAMERA);

    @Rule
    public DenyPermissionRule denyLocation = DenyPermissionRule.deny(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void locationPermissionDenied_showsToast() {
        // Enable auto sunlight estimation to trigger location permission request
        SettingsManager settings = new SettingsManager(ApplicationProvider.getApplicationContext());
        settings.setAutoSunlightEstimationEnabled(true);

        // Navigate to measurement screen
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_growschedule.R.id.startMeasureButton))
                .perform(ViewActions.click());

        // Assert location permission toast
        final Activity[] activity = new Activity[1];
        activityRule.getScenario().onActivity(a -> activity[0] = a);
        Espresso.onView(ViewMatchers.withText(de.omagh.feature_measurement.R.string.location_permission_required))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void cameraPermissionDenied_showsToast() {
        // Navigate to measurement screen
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_growschedule.R.id.startMeasureButton))
                .perform(ViewActions.click());

        // Attempt to use camera measurement without permission
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_measurement.R.id.cameraMeasureButton))
                .perform(ViewActions.click());

        // Assert camera permission toast
        final Activity[] activity = new Activity[1];
        activityRule.getScenario().onActivity(a -> activity[0] = a);
        Espresso.onView(ViewMatchers.withText(de.omagh.feature_measurement.R.string.camera_permission_required))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
