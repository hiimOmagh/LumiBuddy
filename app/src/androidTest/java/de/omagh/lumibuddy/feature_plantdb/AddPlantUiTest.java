package de.omagh.lumibuddy.feature_plantdb;

import android.Manifest;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.lumibuddy.ui.MainActivity;

@RunWith(AndroidJUnit4.class)
public class AddPlantUiTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES
    );

    @Test
    public void addPlant_showsInList() {
        // Navigate to the plant list
        Espresso.onView(ViewMatchers.withId(de.omagh.lumibuddy.R.id.plantListFragment))
                .perform(ViewActions.click());

        // Open add plant dialog
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.addPlantFab))
                .perform(ViewActions.click());

        // Fill in plant name and type
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantName))
                .perform(ViewActions.typeText("testPlantName"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.editPlantType))
                .perform(ViewActions.typeText("testPlantType"), ViewActions.closeSoftKeyboard());

        // Confirm addition
        Espresso.onView(ViewMatchers.withText("Add")).perform(ViewActions.click());

        // Assert the new plant appears in the list
        Espresso.onView(ViewMatchers.withId(de.omagh.feature_plantdb.R.id.plantRecyclerView))
                .perform(RecyclerViewActions.scrollTo(
                        ViewMatchers.hasDescendant(ViewMatchers.withText("testPlantName"))
                ));
    }
}
