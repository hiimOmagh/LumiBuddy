package de.omagh.feature_diary.ui;

import static org.mockito.Mockito.*;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.sync.DiarySyncManager;
import de.omagh.core_infra.sync.SyncStatus;

public class DiaryViewModelTest {
    @Test
    public void triggerSync_invokesSyncManager() {
        Application app = ApplicationProvider.getApplicationContext();
        DiaryRepository repo = mock(DiaryRepository.class);
        DiarySyncManager manager = mock(DiarySyncManager.class);
        when(manager.getSyncStatus()).thenReturn(new MutableLiveData<>(SyncStatus.IDLE));
        when(manager.getError()).thenReturn(new MutableLiveData<>());

        DiaryViewModel vm = new DiaryViewModel(app, repo, manager);
        vm.triggerSync();

        verify(manager, times(1)).sync();
    }
}
