package de.omagh.lumibuddy.feature_recommendation;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_infra.recommendation.NotificationManager;
import de.omagh.core_infra.recommendation.RecommendationEngine;
import de.omagh.core_infra.recommendation.WateringScheduler;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Integration test ensuring watering reminders trigger.
 */
@RunWith(AndroidJUnit4.class)
public class WateringSchedulerTest {
    private Context context;
    private DiaryDao diaryDao;
    private NotificationManager notifications;
    private WateringScheduler scheduler;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        diaryDao = mock(DiaryDao.class);
        notifications = mock(NotificationManager.class);
        scheduler = new WateringScheduler(new RecommendationEngine(), notifications, diaryDao);
    }

    /**
     * When no watering event exists, a notification should be sent.
     */
    @Test
    public void notifyWhenOverdue() throws Exception {
        Plant plant = new Plant("1", "Basil", "Basil", "");
        when(diaryDao.getEntriesForPlantSync("1")).thenReturn(new ArrayList<>());
        CountDownLatch notifyLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            notifyLatch.countDown();
            return null;
        }).when(notifications).notifyWateringNeeded(eq(plant), anyInt());

        CountDownLatch insertLatch = new CountDownLatch(1);
        doAnswer(invocation -> {
            insertLatch.countDown();
            return null;
        }).when(diaryDao).insert(any(DiaryEntry.class));
        scheduler.runDailyCheck(Collections.singletonList(plant));
        assertTrue(notifyLatch.await(1, TimeUnit.SECONDS));
        assertTrue(insertLatch.await(1, TimeUnit.SECONDS));
        verify(notifications).notifyWateringNeeded(eq(plant), anyInt());
        verify(diaryDao).insert(any(DiaryEntry.class));
    }
}