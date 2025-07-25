package de.omagh.feature_diary;

import static org.junit.Assert.assertSame;

import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_domain.sync.SyncScheduler;
import de.omagh.core_domain.util.AppExecutors;

/**
 * Simple unit tests for {@link DiaryRepository} in this feature module.
 */
public class DiaryRepositoryTest {
    @Mock
    DiaryDao dao;
    DiaryRepository repository;
    AppExecutors executors;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        executors = Mockito.mock(AppExecutors.class);
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        Mockito.when(executors.single()).thenReturn(executor);
        android.content.Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        SyncScheduler scheduler = Mockito.mock(SyncScheduler.class);
        repository = new DiaryRepository(context, dao, executors, scheduler);
    }

    @Test
    public void insert_delegatesToDao() {
        DiaryEntry entry = new DiaryEntry("1", "p1", 0L, "", "", "watering");
        repository.insert(entry);
        Mockito.verify(dao, Mockito.timeout(1000)).insert(entry);
    }

    @Test
    public void getEntries_returnsDaoLiveData() {
        MutableLiveData<List<DiaryEntry>> live = new MutableLiveData<>();
        Mockito.when(dao.getAllEntries()).thenReturn(live);
        assertSame(live, repository.getAllEntries());
    }

    @Test
    public void delete_delegatesToDao() {
        DiaryEntry entry = new DiaryEntry("2", "p1", 0L, "", "", "watering");
        repository.delete(entry);
        Mockito.verify(dao, Mockito.timeout(1000)).delete(entry);
    }

    @Test
    public void update_delegatesToDao() {
        DiaryEntry entry = new DiaryEntry("3", "p1", 0L, "", "", "watering");
        repository.update(entry);
        Mockito.verify(dao, Mockito.timeout(1000)).update(entry);
    }

    @Test
    public void getAllEntriesSync_delegatesToDao() {
        java.util.List<DiaryEntry> list = java.util.Collections.emptyList();
        Mockito.when(dao.getAllEntriesSync()).thenReturn(list);
        assertSame(list, repository.getAllEntriesSync());
    }
}
