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
import de.omagh.core_domain.util.AppExecutors;

/**
 * Simple unit tests for {@link DiaryRepository} in this feature module.
 */
public class DiaryRepositoryTest {
    @Mock
    DiaryDao dao;
    DiaryRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        repository = new DiaryRepository(dao, new AppExecutors());
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
}