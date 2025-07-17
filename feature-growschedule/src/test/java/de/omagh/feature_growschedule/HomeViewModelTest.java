package de.omagh.feature_growschedule;

import static org.junit.Assert.assertSame;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import java.util.List;

import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_data.repository.TaskDataSource;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.Task;
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
    TaskDataSource taskRepo;
    @Mock
    RecommendationEngine engine;
    @Mock
    WateringScheduler scheduler;
    @Mock
    MutableLiveData<List<Plant>> plantLive;
    @Mock
    MutableLiveData<List<Task>> taskLive;

    private Application app;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        app = ApplicationProvider.getApplicationContext();
        Mockito.when(plantRepo.getAllPlants()).thenReturn(plantLive);
        Mockito.when(taskRepo.getPendingTasks()).thenReturn(taskLive);
    }

    @Test
    public void getPlants_returnsRepositoryLiveData() {
        HomeViewModel vm = new HomeViewModel(app, plantRepo, diaryRepo, taskRepo, engine, scheduler);
        assertSame(plantLive, vm.getPlants());
    }

    @Test
    public void clearingViewModel_removesObserver() {
        TestHomeViewModel vm = new TestHomeViewModel(app, plantRepo, diaryRepo, taskRepo, engine, scheduler);

        ArgumentCaptor<Observer<List<Plant>>> captor = ArgumentCaptor.forClass(Observer.class);
        Mockito.verify(plantLive).observeForever(captor.capture());
        Observer<List<Plant>> obs = captor.getValue();

        vm.invokeOnCleared();

        Mockito.verify(plantLive).removeObserver(obs);
    }

    private static class TestHomeViewModel extends HomeViewModel {
        TestHomeViewModel(Application app, PlantDataSource plantRepo, DiaryDataSource diaryRepo,
                          TaskDataSource taskRepo, RecommendationEngine engine, WateringScheduler scheduler) {
            super(app, plantRepo, diaryRepo, taskRepo, engine, scheduler);
        }

        void invokeOnCleared() {
            super.onCleared();
        }
    }
}
