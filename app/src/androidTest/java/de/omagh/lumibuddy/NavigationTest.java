package de.omagh.lumibuddy;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
        // TODO: add real navigation assertions
        rule.getScenario().onActivity(activity -> {
            // Add navigation assertions in future
        });
    }
}
