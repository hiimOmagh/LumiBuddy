package de.omagh.lumibuddy.feature_plantdb;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.lumibuddy.ui.MainActivity;

/**
 * Instrumentation test verifying that deleting a plant removes it from the list.
 */
@RunWith(AndroidJUnit4.class)
public class DeletePlantUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
    );

    @Before
    public void addPlant() {
        // Navigate to the plant list
        Espresso.onView(ViewMatchers.withId(de.omagh.lumibuddy.R.id.plantListFragment))
                .perform(ViewActions.click());

        // Add a plant through the existing add flow
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.addPlantFab))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantName))
                .perform(ViewActions.replaceText("plantToDelete"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantType))
                .perform(ViewActions.replaceText("plantType"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withText("Add")).perform(ViewActions.click());
    }

    @Test
    public void deletePlant_removesFromList() {
        // Ensure we are on the plant list
        Espresso.onView(ViewMatchers.withId(de.omagh.lumibuddy.R.id.plantListFragment))
                .perform(ViewActions.click());

        // Long press the inserted plant and confirm deletion
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.plantRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("plantToDelete")),
                        ViewActions.longClick()));

        Espresso.onView(ViewMatchers.withText("Delete")).perform(ViewActions.click());

        // Verify the plant is removed from the list
        Espresso.onView(ViewMatchers.withText("plantToDelete"))
                .check(ViewAssertions.doesNotExist());
    }
}
