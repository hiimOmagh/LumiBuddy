package de.omagh.lumibuddy.feature_diary;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.lifecycle.MutableLiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import de.omagh.core_data.model.Task;
import de.omagh.core_data.repository.TaskRepository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class AgendaManagerTest {
    @Mock
    Context context;
    @Mock
    ContentResolver resolver;
    @Mock
    TaskRepository repository;

    private AgendaManager manager;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(context.getApplicationContext()).thenReturn(context);
        when(context.getContentResolver()).thenReturn(resolver);
        manager = new AgendaManager(context, repository);
    }

    @Test
    public void addTask_delegatesToRepository() {
        Task t = new Task("1", "p", "desc", 1L, false);
        manager.addTask(t, false);
        verify(repository).insert(t);
        verifyNoInteractions(resolver);
    }

    @Test
    public void addTask_addsCalendarEventWhenRequested() {
        Task t = new Task("1", "p", "desc", 1L, false);
        manager.addTask(t, true);
        verify(repository).insert(t);
        verify(resolver).insert(eq(CalendarContract.Events.CONTENT_URI), any());
    }

    @Test
    public void getPendingTasks_returnsRepoLiveData() {
        MutableLiveData<List<Task>> live = new MutableLiveData<>(Collections.emptyList());
        when(repository.getPendingTasks()).thenReturn(live);
        assertSame(live, manager.getPendingTasks());
    }
}
