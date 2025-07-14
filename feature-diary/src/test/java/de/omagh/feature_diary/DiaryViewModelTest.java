package de.omagh.feature_diary;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.feature_diary.ui.DiaryViewModel;
import de.omagh.core_infra.sync.DiarySyncManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

public class DiaryViewModelTest {
    @Mock
    DiaryRepository repository;
    @Mock
    DiarySyncManager syncManager;

    private DiaryViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(repository.getAllEntries()).thenReturn(new MutableLiveData<>(Collections.emptyList()));
        Mockito.when(syncManager.getSyncStatus()).thenReturn(new MutableLiveData<>());
        Mockito.when(syncManager.getError()).thenReturn(new MutableLiveData<>());
        vm = new DiaryViewModel(app, repository, syncManager);
    }

    @Test
    public void getAllEntries_returnsRepositoryLiveData() {
        assertSame(repository.getAllEntries(), vm.getAllEntries());
    }

    @Test
    public void addEntry_delegatesToRepo() {
        DiaryEntry e = new DiaryEntry("1", "p1", 0L, "", "", "e");
        vm.addEntry(e);
        Mockito.verify(repository).insert(e);
    }
}
