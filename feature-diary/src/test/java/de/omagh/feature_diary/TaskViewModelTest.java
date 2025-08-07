package de.omagh.feature_diary;

import static org.mockito.Mockito.*;

import android.app.Application;

import androidx.lifecycle.Observer;
import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import de.omagh.core_data.model.Task;
import de.omagh.core_data.repository.TaskRepository;
import de.omagh.feature_diary.ui.TaskViewModel;

public class TaskViewModelTest {
    @Mock
    TaskRepository repository;
    private TaskViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        vm = new TaskViewModel(app, repository);
    }

    @Test
    public void loadTasks_postsMockedTasks() {
        List<Task> tasks = Arrays.asList(
                new Task("1", "p1", "Water", 0L, false),
                new Task("2", "p1", "Fertilize", 0L, false)
        );
        when(repository.getPendingTasksSync()).thenReturn(tasks);
        Observer<List<Task>> observer = mock(Observer.class);
        vm.getTasks().observeForever(observer);

        vm.loadTasks();

        verify(observer).onChanged(tasks);
    }
}
