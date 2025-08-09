package de.omagh.lumibuddy.feature_diary;

import android.app.Activity;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.feature_diary.R;
import de.omagh.feature_diary.ui.dialog.DiaryEntryDialog;

import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class DiaryEntryValidationTest {

    @Test
    public void saveWithMissingData_showsError_andNoEntry() {
        try (ActivityScenario<ComponentActivity> scenario = ActivityScenario.launch(ComponentActivity.class)) {
            final boolean[] called = {false};
            scenario.onActivity(activity -> DiaryEntryDialog.show(activity, entry -> called[0] = true));

            // Attempt to save without entering note or type
            Espresso.onView(ViewMatchers.withText(R.string.save)).perform(ViewActions.click());

            // Verify toast is shown
            final Activity[] activity = new Activity[1];
            scenario.onActivity(a -> activity[0] = a);
            Espresso.onView(ViewMatchers.withText(R.string.diary_entry_validation_error))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(Matchers.is(activity[0].getWindow().getDecorView()))))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            // Ensure listener was not called
            assertFalse(called[0]);
        }
    }
}
