package de.omagh.lumibuddy.feature_plantdb;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.lumibuddy.ui.MainActivity;

/**
 * Instrumentation test verifying that editing a plant updates it in the list.
 */
@RunWith(AndroidJUnit4.class)
public class EditPlantUiTest {

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
                .perform(ViewActions.replaceText("originalName"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantType))
                .perform(ViewActions.replaceText("originalType"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withText("Add")).perform(ViewActions.click());
    }

    @Test
    public void editPlant_updatesList() {
        // Ensure we are on the plant list
        Espresso.onView(ViewMatchers.withId(de.omagh.lumibuddy.R.id.plantListFragment))
                .perform(ViewActions.click());

        // Open details of the inserted plant
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.plantRecyclerView))
                .perform(RecyclerViewActions.actionOnItem(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("originalName")),
                        ViewActions.click()));

        // Tap edit FAB to open dialog
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantFab))
                .perform(ViewActions.click());

        // Update name and type
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantName))
                .perform(ViewActions.replaceText("editedName"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantType))
                .perform(ViewActions.replaceText("editedType"), ViewActions.closeSoftKeyboard());

        // Save changes
        Espresso.onView(ViewMatchers.withText("Save")).perform(ViewActions.click());

        // Navigate back to list
        Espresso.pressBack();

        // Assert updated plant name is displayed
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.plantRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("editedName"))
                ));
    }
}
