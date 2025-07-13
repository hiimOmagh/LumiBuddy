package de.omagh.core_infra.sync;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.repository.firebase.FirestorePlantDao;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_infra.user.SettingsManager;

public class PlantSyncManagerTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void resolveConflicts_remoteWins() {
        Plant local = new Plant("1", "A", "t", "");
        Plant remote = new Plant("1", "B", "t", "");
        PlantSyncManager mgr = new PlantSyncManager(
                Mockito.mock(PlantRepository.class),
                Mockito.mock(FirestorePlantDao.class),
                Mockito.mock(FirebaseManager.class),
                Mockito.mock(SettingsManager.class),
                new AppExecutors());
        java.util.List<Plant> result = mgr.resolveConflicts(
                Collections.singletonList(local),
                Collections.singletonList(remote));
        assertEquals("B", result.get(0).getName());
    }

    @Test
    public void sync_errorUpdatesStatus() throws Exception {
        PlantRepository repo = Mockito.mock(PlantRepository.class);
        FirestorePlantDao cloud = Mockito.mock(FirestorePlantDao.class);
        FirebaseManager fb = Mockito.mock(FirebaseManager.class);
        Mockito.when(fb.signInAnonymously()).thenReturn(Tasks.forException(new RuntimeException("fail")));
        SettingsManager settings = Mockito.mock(SettingsManager.class);
        PlantSyncManager mgr = new PlantSyncManager(repo, cloud, fb, settings, new AppExecutors(java.util.concurrent.Executors.newSingleThreadExecutor(), java.util.concurrent.Executors.newSingleThreadExecutor()));
        mgr.sync();
        // allow task
        Thread.sleep(100);
        assertEquals(SyncStatus.ERROR, mgr.getSyncStatus().getValue());
    }
}
