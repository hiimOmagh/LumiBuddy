package de.omagh.core_data;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
import de.omagh.core_domain.util.AppExecutors;

/**
 * Tests for {@link DiaryRepository} using Mockito.
 */
public class DiaryRepositoryMockitoTest {
    @Mock
    private DiaryDao dao;

    private DiaryRepository repository;
    private AppExecutors executors;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        executors = Mockito.mock(AppExecutors.class);
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
        Mockito.when(executors.single()).thenReturn(executor);
        android.content.Context context = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        repository = new DiaryRepository(context, dao, executors);
    }

    @Test
    public void insert_callsDao() {
        DiaryEntry entry = new DiaryEntry("1", "p1", 1L, "note", "", "watering");
        repository.insert(entry);
        Mockito.verify(dao, Mockito.timeout(1000)).insert(entry);
    }

    @Test
    public void delete_callsDao() {
        DiaryEntry entry = new DiaryEntry("1", "p1", 1L, "note", "", "watering");
        repository.delete(entry);
        Mockito.verify(dao, Mockito.timeout(1000)).delete(entry);
    }

    @Test
    public void getEntries_returnsLiveDataFromDao() {
        MutableLiveData<List<DiaryEntry>> live = new MutableLiveData<>();
        Mockito.when(dao.getEntriesForPlant("p1")).thenReturn(live);
        assertSame(live, repository.getEntriesForPlant("p1"));
    }

    @Test
    public void getAllEntries_callsDao() {
        MutableLiveData<List<DiaryEntry>> live = new MutableLiveData<>();
        Mockito.when(dao.getAllEntries()).thenReturn(live);
        assertSame(live, repository.getAllEntries());
        Mockito.verify(dao).getAllEntries();
    }

    @Test
    public void getEntriesForPlantSync_callsDao() {
        List<DiaryEntry> list = java.util.Collections.emptyList();
        Mockito.when(dao.getEntriesForPlantSync("p1")).thenReturn(list);
        assertSame(list, repository.getEntriesForPlantSync("p1"));
        Mockito.verify(dao).getEntriesForPlantSync("p1");
    }

    @Test
    public void getAllEntriesSync_callsDao() {
        List<DiaryEntry> list = java.util.Collections.emptyList();
        Mockito.when(dao.getAllEntriesSync()).thenReturn(list);
        assertSame(list, repository.getAllEntriesSync());
        Mockito.verify(dao).getAllEntriesSync();
    }

    @Test
    public void shutdown_closesExecutor() {
        repository.shutdown();
        java.util.concurrent.ExecutorService e = executors.single();
        assertTrue(e.isShutdown());
    }
}