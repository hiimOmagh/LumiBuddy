package de.omagh.lumibuddy.feature_plantdb;

import android.Manifest;
import android.app.Activity;

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

import de.omagh.lumibuddy.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class AddPlantPermissionTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public DenyPermissionRule denyCamera = DenyPermissionRule.deny(Manifest.permission.CAMERA);

    @Test
    public void captureWithoutPermission_showsToast() {
        // Navigate to the plant list
        Espresso.onView(ViewMatchers.withId(de.omagh.lumibuddy.R.id.plantListFragment))
                .perform(ViewActions.click());

        // Open add plant dialog
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.addPlantFab))
                .perform(ViewActions.click());

        // Attempt to capture image without permission
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.captureImageBtn))
                .perform(ViewActions.click());

        // Assert toast indicating permission is required
        final Activity[] activity = new Activity[1];
        activityRule.getScenario().onActivity(a -> activity[0] = a);
        Espresso.onView(ViewMatchers.withText(de.omagh.feature_plantdb.R.string.camera_permission_required))
                .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
