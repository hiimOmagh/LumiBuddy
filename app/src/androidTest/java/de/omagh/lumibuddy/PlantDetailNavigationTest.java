package de.omagh.lumibuddy;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.lumibuddy.ui.MainActivity;

/**
 * UI test ensuring navigation from PlantDetailFragment to the diary timeline
 * works when the button is pressed.
 */
@RunWith(AndroidJUnit4.class)
public class PlantDetailNavigationTest {
    @Test
    public void openTimeline_fromDetail() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("openPlantId", "testId");
        intent.putExtra("openPlantName", "Test Plant");
        intent.putExtra("openPlantType", "Type");
        intent.putExtra("openPlantImageUri", "");

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(intent)) {
            Espresso.onView(ViewMatchers.withId(
                            de.omagh.feature_growschedule.R.id.viewTimelineButton))
                    .perform(ViewActions.click());
            Espresso.onView(ViewMatchers.withId(
                            de.omagh.feature_diary.R.id.growthTimelineRecyclerView))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }
}