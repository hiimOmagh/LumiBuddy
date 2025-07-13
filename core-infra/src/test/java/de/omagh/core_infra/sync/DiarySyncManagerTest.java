package de.omagh.core_infra.sync;

import static org.junit.Assert.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.tasks.Tasks;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.firebase.FirestoreDiaryEntryDao;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_infra.user.SettingsManager;

public class DiarySyncManagerTest {
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Test
    public void resolveConflicts_newerTimestampWins() {
        DiaryEntry local = new DiaryEntry("1", "p", 1L, "", "", "note");
        DiaryEntry remote = new DiaryEntry("1", "p", 2L, "", "", "note");
        DiarySyncManager mgr = new DiarySyncManager(
                Mockito.mock(DiaryRepository.class),
                Mockito.mock(FirestoreDiaryEntryDao.class),
                Mockito.mock(FirebaseManager.class),
                Mockito.mock(SettingsManager.class),
                new AppExecutors());
        java.util.List<DiaryEntry> res = mgr.resolveConflicts(
                Collections.singletonList(local),
                Collections.singletonList(remote));
        assertEquals(2L, res.get(0).getTimestamp());
    }

    @Test
    public void sync_errorUpdatesStatus() throws Exception {
        DiaryRepository repo = Mockito.mock(DiaryRepository.class);
        FirestoreDiaryEntryDao cloud = Mockito.mock(FirestoreDiaryEntryDao.class);
        FirebaseManager fb = Mockito.mock(FirebaseManager.class);
        Mockito.when(fb.signInAnonymously()).thenReturn(Tasks.forException(new RuntimeException("fail")));
        SettingsManager settings = Mockito.mock(SettingsManager.class);
        DiarySyncManager mgr = new DiarySyncManager(repo, cloud, fb, settings, new AppExecutors(java.util.concurrent.Executors.newSingleThreadExecutor(), java.util.concurrent.Executors.newSingleThreadExecutor()));
        mgr.sync();
        Thread.sleep(100);
        assertEquals(SyncStatus.ERROR, mgr.getSyncStatus().getValue());
    }
}
