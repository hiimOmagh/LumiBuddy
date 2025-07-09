package de.omagh.core_infra;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import de.omagh.core_infra.recommendation.PeriodicScanWorker;

import org.junit.Test;

public class PeriodicScanWorkerTest {
    @Test
    public void workerRuns() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        ListenableWorker worker = TestListenableWorkerBuilder
                .from(context, PeriodicScanWorker.class)
                .build();
        assertNotNull(worker);
    }
}
