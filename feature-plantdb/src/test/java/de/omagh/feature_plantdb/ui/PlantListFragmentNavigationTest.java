package de.omagh.feature_plantdb.ui;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.sync.SyncStatus;

/**
 * Espresso tests for {@link PlantListFragment} verifying navigation and data binding.
 */
@RunWith(AndroidJUnit4.class)
public class PlantListFragmentNavigationTest {
    private static PlantListViewModel viewModel;

    @Before
    public void setup() {
        viewModel = Mockito.mock(PlantListViewModel.class);
        List<Plant> plants = Collections.singletonList(new Plant("1", "Rose", "Type", ""));
        when(viewModel.getPlants()).thenReturn(new MutableLiveData<>(plants));
        when(viewModel.getSyncStatus()).thenReturn(new MutableLiveData<>(SyncStatus.IDLE));
        when(viewModel.getSyncError()).thenReturn(new MutableLiveData<>(null));
    }

    @Test
    public void clickPlant_navigatesToDetail() {
        FragmentScenario<TestFragment> scenario = FragmentScenario.launchInContainer(TestFragment.class);
        scenario.onFragment(fragment -> {
            TestNavHostController navController = new TestNavHostController(ApplicationProvider.getApplicationContext());
            navController.setGraph(de.omagh.feature_plantdb.R.navigation.nav_graph);
            navController.setCurrentDestination(de.omagh.feature_plantdb.R.id.plantListFragment);
            Navigation.setViewNavController(fragment.requireView(), navController);
        });

        onView(withText("Rose")).check(matches(isDisplayed()));
        onView(withText("Rose")).perform(click());

        scenario.onFragment(fragment -> {
            TestNavHostController navController = (TestNavHostController) Navigation.findNavController(fragment.requireView());
            assertEquals(de.omagh.feature_plantdb.R.id.plantDetailFragment, navController.getCurrentDestination().getId());
        });
    }

    /**
     * Test fragment overriding dependency injection.
     */
    public static class TestFragment extends PlantListFragment {
        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            this.viewModelFactory = new PlantDbViewModelFactory(() -> viewModel, () -> null, () -> null);
        }
    }
}
