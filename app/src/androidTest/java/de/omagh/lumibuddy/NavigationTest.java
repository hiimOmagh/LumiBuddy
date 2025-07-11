package de.omagh.lumibuddy;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import de.omagh.lumibuddy.ui.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NavigationTest {
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void startDestination_isDisplayed() {
        Espresso.onView(ViewMatchers.withId(
                        de.omagh.feature_growschedule.R.id.homeWelcomeText))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(
                        de.omagh.feature_growschedule.R.id.startMeasureButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void navigateToMeasureFragment() {
        Espresso.onView(ViewMatchers.withId(
                        de.omagh.feature_growschedule.R.id.startMeasureButton))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(
                        de.omagh.feature_measurement.R.id.cameraMeasureButton))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
