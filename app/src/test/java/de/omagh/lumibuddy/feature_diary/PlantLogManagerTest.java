package de.omagh.lumibuddy.feature_diary;

import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PlantLogManagerTest {
    @Mock
    DiaryRepository repository;

    private PlantLogManager manager;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        manager = new PlantLogManager(repository);
    }

    @Test
    public void addEntry_delegatesToRepo() {
        DiaryEntry e = new DiaryEntry("1", "p", 0L, "", "", "note");
        manager.addEntry(e);
        verify(repository).insert(e);
    }

    @Test
    public void deleteEntry_delegatesToRepo() {
        DiaryEntry e = new DiaryEntry("1", "p", 0L, "", "", "note");
        manager.deleteEntry(e);
        verify(repository).delete(e);
    }

    @Test
    public void getEntries_returnsRepoLiveData() {
        MutableLiveData<List<DiaryEntry>> live = new MutableLiveData<>(Collections.emptyList());
        when(repository.getAllEntries()).thenReturn(live);
        assertSame(live, manager.getAllEntries());
    }
}
