package de.omagh.core_infra.sync;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.Configuration;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.WorkManagerTestInitHelper;
import androidx.work.testing.TestDriver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class SyncSchedulerTest {
    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        Configuration config = new Configuration.Builder().build();
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @After
    public void teardown() {
        WorkManager.getInstance(context).cancelAllWork();
    }

    @Test
    public void fullSync_runsAfterConstraintsMet() throws Exception {
        SyncScheduler scheduler = new SyncScheduler(context);
        scheduler.scheduleDaily();

        WorkManager wm = WorkManager.getInstance(context);
        List<WorkInfo> infos = wm.getWorkInfosForUniqueWork("FullSyncWorker").get();
        assertEquals(1, infos.size());
        WorkInfo info = infos.get(0);
        assertEquals(WorkInfo.State.ENQUEUED, info.getState());

        TestDriver driver = WorkManagerTestInitHelper.getTestDriver(context);
        assertNotNull(driver);
        driver.setAllConstraintsMet(info.getId());
        driver.setPeriodDelayMet(info.getId());

        info = wm.getWorkInfoById(info.getId()).get();
        assertEquals(WorkInfo.State.SUCCEEDED, info.getState());
    }
}
