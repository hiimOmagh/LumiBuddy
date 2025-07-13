package de.omagh.lumibuddy;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.lumibuddy.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class OnboardingFlowTest {
    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE).edit().clear().commit();
    }

    @Test
    public void showsOnboardingOnFirstRun() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.onboardingRoot))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void completesOnboardingAndOpensMain() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.skipButton)).perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    @Test
    public void skipsOnboardingWhenCompleted() {
        context.getSharedPreferences("user_settings", Context.MODE_PRIVATE)
                .edit().putBoolean("onboarding_complete", true).commit();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            Espresso.onView(ViewMatchers.withId(R.id.bottomNavigationView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}
