package de.omagh.core_infra.recommendation;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

public class WateringWorkerTest {
    @Test
    public void workerInstantiates() {
        Context context = ApplicationProvider.getApplicationContext();
        ListenableWorker worker = TestListenableWorkerBuilder
                .from(context, WateringWorker.class)
                .build();
        assertNotNull(worker);
    }
}
