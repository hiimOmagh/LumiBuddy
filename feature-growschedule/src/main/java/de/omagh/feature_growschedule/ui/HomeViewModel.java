package de.omagh.feature_growschedule.ui;

import android.Manifest;
import android.app.Application;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.recommendation.RecommendationEngine;
import de.omagh.core_infra.recommendation.WateringScheduler;

import javax.inject.Inject;

/**
 * ViewModel coordinating home screen data and background recommendation logic.
 */
public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> welcomeText = new MutableLiveData<>("Welcome to LumiBuddy!");
    private final MutableLiveData<Float> lux = new MutableLiveData<>(650f);
    private final MutableLiveData<Float> ppfd = new MutableLiveData<>(35f);
    private final MutableLiveData<Float> dli = new MutableLiveData<>(12.0f);

    private final DiaryDataSource diaryRepository;
    private final RecommendationEngine recommendationEngine;
    private final WateringScheduler wateringScheduler;
    private final LiveData<List<Plant>> plantsLiveData;
    private final MutableLiveData<List<String>> reminders = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();


    /**
     * Constructs the ViewModel with injected repositories and helpers.
     */
    @Inject
    public HomeViewModel(Application application,
                         PlantDataSource plantRepository,
                         DiaryDataSource diaryRepository,
                         RecommendationEngine engine,
                         WateringScheduler scheduler) {
        super(application);
        this.diaryRepository = diaryRepository;
        this.recommendationEngine = engine;
        this.wateringScheduler = scheduler;
        plantsLiveData = plantRepository.getAllPlants();
        plantsLiveData.observeForever(this::launchChecks);
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void launchChecks(List<Plant> plants) {
        executor.submit(() -> runChecks(plants));
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private void runChecks(List<Plant> plants) {
        if (plants == null || plants.isEmpty()) {
            reminders.postValue(new ArrayList<>());
            return;
        }
        Map<String, List<DiaryEntry>> diaryMap = new HashMap<>();
        for (Plant p : plants) {
            diaryMap.put(p.getId(), diaryRepository.getEntriesForPlantSync(p.getId()));
        }
        List<Plant> due = recommendationEngine.getPlantsNeedingWater(plants, diaryMap);
        List<String> tasks = new ArrayList<>();
        for (Plant p : due) {
            tasks.add("Water " + p.getName());
        }
        reminders.postValue(tasks);
        wateringScheduler.runDailyCheck(plants);
        recommendationEngine.checkLightRecommendations(plants,
                diaryRepository::getEntriesForPlantSync,
                diaryRepository::insert);
    }

    public LiveData<String> getWelcomeText() {
        return welcomeText;
    }

    public LiveData<Float> getLux() {
        return lux;
    }

    public LiveData<Float> getPpfd() {
        return ppfd;
    }

    public LiveData<Float> getDli() {
        return dli;
    }

    /**
     * Triggers a manual refresh of reminder calculations using the currently
     * loaded plant list.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void refresh() {
        List<Plant> current = plantsLiveData.getValue();
        if (current != null) {
            launchChecks(current);
        }
    }

    public LiveData<List<Plant>> getPlants() {
        return plantsLiveData;
    }

    public LiveData<List<String>> getUpcomingReminders() {
        return reminders;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (diaryRepository instanceof DiaryRepository) {
            ((DiaryRepository) diaryRepository).shutdown();
        }
        wateringScheduler.shutdown();
        recommendationEngine.shutdown();
        executor.shutdown();
    }
}