package de.omagh.feature_growschedule;

import static org.junit.Assert.assertSame;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_infra.recommendation.RecommendationEngine;
import de.omagh.core_infra.recommendation.WateringScheduler;
import de.omagh.feature_growschedule.ui.HomeViewModel;

/**
 * Unit tests for {@link HomeViewModel} verifying LiveData sources.
 */
public class HomeViewModelTest {
    @Mock
    PlantDataSource plantRepo;
    @Mock
    DiaryDataSource diaryRepo;
    @Mock
    RecommendationEngine engine;
    @Mock
    WateringScheduler scheduler;
    @Mock
    MutableLiveData<List<Plant>> plantLive;

    private Application app;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        app = ApplicationProvider.getApplicationContext();
        Mockito.when(plantRepo.getAllPlants()).thenReturn(plantLive);
    }

    @Test
    public void getPlants_returnsRepositoryLiveData() {
        HomeViewModel vm = new HomeViewModel(app, plantRepo, diaryRepo, engine, scheduler);
        assertSame(plantLive, vm.getPlants());
    }
}