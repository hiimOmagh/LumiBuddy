package de.omagh.core_data;

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

/**
 * Tests for {@link DiaryRepository} using Mockito.
 */
public class DiaryRepositoryMockitoTest {
    @Mock
    private DiaryDao dao;

    private DiaryRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new DiaryRepository(dao);
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
}