package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;

import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_data.db.TaskDao;
import de.omagh.core_data.model.Task;

public class TaskRepository implements TaskDataSource {
    private final TaskDao taskDao;
    private final ExecutorService executor;

    public TaskRepository(TaskDao dao, AppExecutors executors) {
        this.taskDao = dao;
        this.executor = executors.single();
    }

    @Override
    public LiveData<List<Task>> getTasksForPlant(String plantId) {
        return taskDao.getTasksForPlant(plantId);
    }

    @Override
    public List<Task> getTasksForPlantSync(String plantId) {
        return taskDao.getTasksForPlantSync(plantId);
    }

    @Override
    public LiveData<List<Task>> getPendingTasks() {
        return taskDao.getPendingTasks();
    }

    @Override
    public List<Task> getPendingTasksSync() {
        return taskDao.getPendingTasksSync();
    }

    @Override
    public void insert(Task task) {
        executor.execute(() -> taskDao.insert(task));
    }

    @Override
    public void update(Task task) {
        executor.execute(() -> taskDao.update(task));
    }

    @Override
    public void delete(Task task) {
        executor.execute(() -> taskDao.delete(task));
    }

    public void shutdown() {
        executor.shutdown();
    }
}
