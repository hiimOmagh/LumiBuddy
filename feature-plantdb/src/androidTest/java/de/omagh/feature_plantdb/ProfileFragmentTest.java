package de.omagh.feature_plantdb;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import de.omagh.feature_plantdb.ui.ProfileFragment;
import de.omagh.feature_plantdb.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ProfileFragmentTest {

    @Test
    public void displaysStoredUserName() {
        Context context = ApplicationProvider.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE);
        prefs.edit().putString("profile_name", "Test User").apply();

        FragmentScenario.launchInContainer(ProfileFragment.class);

        onView(withId(R.id.profileName)).check(matches(withText("Test User")));
    }
}
