package de.omagh.lumibuddy.feature_recommendation;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;

import de.omagh.lumibuddy.data.db.DiaryDao;
import de.omagh.core_domain.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

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
        scheduler.runDailyCheck(Collections.singletonList(plant));
        Thread.sleep(500);
        verify(notifications, timeout(1000)).notifyWateringNeeded(eq(plant), anyInt());
        verify(diaryDao, timeout(1000)).insert(any(DiaryEntry.class));
    }
}