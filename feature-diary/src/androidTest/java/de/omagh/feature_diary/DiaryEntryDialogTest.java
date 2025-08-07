package de.omagh.feature_diary;

import androidx.activity.ComponentActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.feature_diary.ui.dialog.DiaryEntryDialog;
import de.omagh.feature_diary.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class DiaryEntryDialogTest {

    @Test
    public void showsDialogTitle() {
        try (ActivityScenario<ComponentActivity> scenario = ActivityScenario.launch(ComponentActivity.class)) {
            scenario.onActivity(activity -> DiaryEntryDialog.show(activity, entry -> {}));

            onView(withText(R.string.add_diary_entry))
                    .check(matches(withText(R.string.add_diary_entry)));
        }
    }
}
