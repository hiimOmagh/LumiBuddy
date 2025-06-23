package de.omagh.lumibuddy.feature_diary;

import androidx.lifecycle.MutableLiveData;

import de.omagh.core_data.db.DiaryDao;
import de.omagh.core_data.model.DiaryEntry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DiaryRepository}.
 */
public class DiaryRepositoryTest {
    @Mock
    private DiaryDao dao;

    private DiaryRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new DiaryRepository(dao);
    }

    /**
     * Verifies that inserting an entry delegates to the DAO.
     */
    @Test
    public void insert_callsDao() {
        DiaryEntry e = new DiaryEntry("1", "p1", 1L, "note", "", "watering");
        repository.insert(e);
        Mockito.verify(dao, Mockito.timeout(1000)).insert(e);
    }

    /**
     * Verifies that deletion delegates to the DAO.
     */
    @Test
    public void delete_callsDao() {
        DiaryEntry e = new DiaryEntry("1", "p1", 1L, "note", "", "watering");
        repository.delete(e);
        Mockito.verify(dao, Mockito.timeout(1000)).delete(e);
    }

    /**
     * Ensures getEntriesForPlant returns the DAO LiveData.
     */
    @Test
    public void getEntries_returnsDaoLiveData() {
        MutableLiveData<List<DiaryEntry>> live = new MutableLiveData<>();
        Mockito.when(dao.getEntriesForPlant("p1")).thenReturn(live);
        assertSame(live, repository.getEntriesForPlant("p1"));
    }

    /**
     * Ensures synchronous fetch returns the DAO list.
     */
    @Test
    public void getEntriesSync_returnsDaoList() {
        List<DiaryEntry> list = Arrays.asList(new DiaryEntry("1", "p1", 1L, "n", "", ""));
        Mockito.when(dao.getEntriesForPlantSync("p1")).thenReturn(list);
        assertSame(list, repository.getEntriesForPlantSync("p1"));
    }
}