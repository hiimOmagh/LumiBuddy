package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.omagh.core_data.model.Task;

public interface TaskDataSource {
    LiveData<List<Task>> getTasksForPlant(String plantId);

    List<Task> getTasksForPlantSync(String plantId);

    LiveData<List<Task>> getPendingTasks();

    List<Task> getPendingTasksSync();

    void insert(Task task);

    void update(Task task);

    void delete(Task task);
}
