package de.omagh.feature_growschedule;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AgendaFragmentTest {

    @Test
    public void showsAgendaTitle() {
        FragmentScenario.launchInContainer(AgendaFragment.class);

        onView(withId(R.id.agendaTitle)).check(matches(withText(R.string.agenda_title)));
    }
}
