package de.omagh.core_infra.sync;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.work.Configuration;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;

import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;

/**
 * Sample unit test for {@link FullSyncWorker} invoking both sync managers.
 */
public class FullSyncWorkerTest {
    private PlantSyncManager plantSyncManager;
    private DiarySyncManager diarySyncManager;

    @Before
    public void setup() {
        plantSyncManager = mock(PlantSyncManager.class);
        diarySyncManager = mock(DiarySyncManager.class);

        // Set up a fake CoreComponent returned by the Application context
        CoreComponent core = mock(CoreComponent.class);
        when(core.plantSyncManager()).thenReturn(plantSyncManager);
        when(core.diarySyncManager()).thenReturn(diarySyncManager);

        Context app = ApplicationProvider.getApplicationContext();
        ((CoreComponentProvider) app).getCoreComponent(); // ensure cast is valid
        java.lang.reflect.Field f;
        try {
            f = app.getClass().getDeclaredField("coreComponent");
            f.setAccessible(true);
            f.set(app, core);
        } catch (Exception ignored) {
        }

        WorkManagerTestInitHelper.initializeTestWorkManager(app,
                new Configuration.Builder().build());
    }

    @Test
    public void doWork_triggersBothManagers() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        ListenableWorker worker = TestListenableWorkerBuilder.from(context, FullSyncWorker.class)
                .build();
        worker.startWork().get();
        verify(plantSyncManager).sync();
        verify(diarySyncManager).sync();
    }
}
