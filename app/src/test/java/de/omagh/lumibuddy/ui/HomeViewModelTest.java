package de.omagh.lumibuddy.ui;

import androidx.lifecycle.MutableLiveData;

import de.omagh.core_domain.model.Plant;
import de.omagh.feature_growschedule.ui.HomeViewModel;
import de.omagh.core_data.repository.DiaryDataSource;
import de.omagh.core_data.repository.TaskDataSource;
import de.omagh.core_data.model.Task;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.PlantDataSource;
import de.omagh.core_infra.recommendation.RecommendationEngine;
import de.omagh.core_infra.recommendation.WateringScheduler;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import androidx.lifecycle.Observer;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link HomeViewModel} using fake repositories.
 */
public class HomeViewModelTest {
    private FakePlantDataSource plantData;
    private FakeDiaryDataSource diaryData;
    private FakeWateringScheduler scheduler;
    private HomeViewModel vm;

    @Before
    public void setup() {
        plantData = new FakePlantDataSource();
        diaryData = new FakeDiaryDataSource();
        FakeTaskDataSource taskData = new FakeTaskDataSource();
        scheduler = new FakeWateringScheduler();
        vm = new HomeViewModel(new android.app.Application(),
                plantData,
                diaryData,
                taskData,
                new RecommendationEngine(),
                scheduler);
    }

    /**
     * When plant list is empty, reminders should be empty as well.
     */
    @Test
    public void emptyPlants_producesNoReminders() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Observer<List<String>> observer = list -> latch.countDown();
        vm.getUpcomingReminders().observeForever(observer);
        plantData.setPlants(Collections.emptyList());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        vm.getUpcomingReminders().removeObserver(observer);
        assertTrue(Objects.requireNonNull(vm.getUpcomingReminders().getValue()).isEmpty());
    }

    /**
     * Adding a plant with no diary entries results in a watering reminder.
     */
    @Test
    public void plantWithoutWatering_triggersReminder() throws Exception {
        Plant plant = new Plant("1", "Basil", "Basil", "");
        CountDownLatch latch = new CountDownLatch(1);
        Observer<List<String>> observer = list -> latch.countDown();
        vm.getUpcomingReminders().observeForever(observer);
        plantData.setPlants(Collections.singletonList(plant));
        diaryData.setEntries(plant.getId(), new ArrayList<>());
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        vm.getUpcomingReminders().removeObserver(observer);
        List<String> reminders = vm.getUpcomingReminders().getValue();
        assertNotNull(reminders);
        assertFalse(reminders.isEmpty());
        assertTrue(reminders.get(0).contains("Water"));
        assertTrue(scheduler.wasRun);
    }

    // --- Fake helper classes ---
    private static class FakePlantDataSource implements PlantDataSource {
        private final MutableLiveData<List<Plant>> live = new MutableLiveData<>();

        @Override
        public MutableLiveData<List<Plant>> getAllPlants() {
            return live;
        }

        @Override
        public MutableLiveData<Plant> getPlant(String id) {
            return new MutableLiveData<>();
        }

        @Override
        public void insertPlant(Plant plant) {
        }

        @Override
        public void updatePlant(Plant plant) {
        }

        @Override
        public void deletePlant(Plant plant) {
        }

        void setPlants(List<Plant> p) {
            live.postValue(p);
        }
    }

    private static class FakeDiaryDataSource implements DiaryDataSource {
        private final Map<String, List<DiaryEntry>> map = new HashMap<>();

        @Override
        public MutableLiveData<List<DiaryEntry>> getEntriesForPlant(String plantId) {
            MutableLiveData<List<DiaryEntry>> l = new MutableLiveData<>();
            l.setValue(map.get(plantId));
            return l;
        }

        @Override
        public List<DiaryEntry> getEntriesForPlantSync(String plantId) {
            List<DiaryEntry> list = map.get(plantId);
            return list == null ? new ArrayList<>() : list;
        }

        @Override
        public void insert(DiaryEntry entry) {
        }

        @Override
        public void update(DiaryEntry entry) {

        }

        @Override
        public void delete(DiaryEntry entry) {
        }

        void setEntries(String id, List<DiaryEntry> e) {
            map.put(id, e);
        }
    }

    private static class FakeTaskDataSource implements TaskDataSource {
        @Override
        public MutableLiveData<List<Task>> getTasksForPlant(String plantId) {
            return new MutableLiveData<>(new ArrayList<>());
        }

        @Override
        public List<Task> getTasksForPlantSync(String plantId) {
            return new ArrayList<>();
        }

        @Override
        public MutableLiveData<List<Task>> getPendingTasks() {
            return new MutableLiveData<>(new ArrayList<>());
        }

        @Override
        public List<Task> getPendingTasksSync() {
            return new ArrayList<>();
        }

        @Override
        public void insert(Task task) {
        }

        @Override
        public void update(Task task) {
        }

        @Override
        public void delete(Task task) {
        }
    }

    private static class FakeWateringScheduler extends WateringScheduler {
        boolean wasRun = false;

        FakeWateringScheduler() {
            super(new RecommendationEngine(), null, null);
        }

        @Override
        public void runDailyCheck(List<Plant> plants) {
            wasRun = true;
        }
    }
}