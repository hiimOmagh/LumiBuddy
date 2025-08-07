package de.omagh.core_data;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Collections;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.model.Task;
import de.omagh.core_data.repository.TaskRepository;
import de.omagh.core_domain.util.AppExecutors;

import org.mockito.Mockito;

public class TaskRepositoryTest {
    private AppDatabase db;
    private TaskRepository repository;

    private static class ImmediateExecutorService extends AbstractExecutorService {
        @Override
        public void shutdown() {}

        @Override
        public java.util.List<Runnable> shutdownNow() { return Collections.emptyList(); }

        @Override
        public boolean isShutdown() { return false; }

        @Override
        public boolean isTerminated() { return false; }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) { return true; }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        AppExecutors executors = Mockito.mock(AppExecutors.class);
        ExecutorService executor = new ImmediateExecutorService();
        Mockito.when(executors.single()).thenReturn(executor);
        repository = new TaskRepository(db.taskDao(), executors);
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void insertAndRetrieveTask() {
        Task task = new Task("1", "plant1", "Water", 0L, false);
        repository.insert(task);
        List<Task> tasks = repository.getTasksForPlantSync("plant1");
        assertEquals(1, tasks.size());
        assertEquals("Water", tasks.get(0).getDescription());
    }

    @Test
    public void deleteRemovesTask() {
        Task task = new Task("1", "plant1", "Water", 0L, false);
        repository.insert(task);
        repository.delete(task);
        assertTrue(repository.getTasksForPlantSync("plant1").isEmpty());
    }
}
