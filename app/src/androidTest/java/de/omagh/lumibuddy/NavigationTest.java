package de.omagh.lumibuddy;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

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
        rule.getScenario().onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            org.junit.Assert.assertEquals(R.id.homeFragment, navController.getCurrentDestination().getId());
        });
    }
}
