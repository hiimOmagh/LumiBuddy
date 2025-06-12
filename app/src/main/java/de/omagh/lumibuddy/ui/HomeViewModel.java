package de.omagh.lumibuddy.ui;

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

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryDataSource;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;
import de.omagh.lumibuddy.feature_diary.DiaryRepository;
import de.omagh.lumibuddy.feature_plantdb.PlantDataSource;
import de.omagh.lumibuddy.feature_plantdb.PlantRepository;
import de.omagh.lumibuddy.feature_recommendation.NotificationManager;
import de.omagh.lumibuddy.feature_recommendation.RecommendationEngine;
import de.omagh.lumibuddy.feature_recommendation.WateringScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel coordinating home screen data and background recommendation logic.
 */
public class HomeViewModel extends AndroidViewModel {

    private final MutableLiveData<String> welcomeText = new MutableLiveData<>("Welcome to LumiBuddy!");
    private final MutableLiveData<Float> lux = new MutableLiveData<>(650f);
    private final MutableLiveData<Float> ppfd = new MutableLiveData<>(35f);
    private final MutableLiveData<Float> dli = new MutableLiveData<>(12.0f);

    private final PlantDataSource plantRepository;
    private final DiaryDataSource diaryRepository;
    private final RecommendationEngine recommendationEngine;
    private final WateringScheduler wateringScheduler;

    private final LiveData<List<Plant>> plantsLiveData;
    private final MutableLiveData<List<String>> reminders = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Default constructor used by the app. It creates repositories and helpers
     * using the singleton {@link AppDatabase} instance.
     */
    public HomeViewModel(Application application) {
        this(
                application,
                new PlantRepository(AppDatabase.getInstance(application)),
                new DiaryRepository(AppDatabase.getInstance(application).diaryDao()),
                new RecommendationEngine(),
                new WateringScheduler(
                        new RecommendationEngine(),
                        new NotificationManager(application),
                        AppDatabase.getInstance(application).diaryDao())
        );
    }

    /**
     * Constructor used for tests to supply fake repositories and schedulers.
     */
    public HomeViewModel(Application application,
                         PlantDataSource plantRepository,
                         DiaryDataSource diaryRepository,
                         RecommendationEngine engine,
                         WateringScheduler scheduler) {
        super(application);
        this.plantRepository = plantRepository;
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