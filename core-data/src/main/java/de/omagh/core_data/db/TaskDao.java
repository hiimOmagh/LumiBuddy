package de.omagh.core_data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import de.omagh.core_data.model.Task;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM tasks WHERE plantId = :plantId AND completed = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getTasksForPlant(String plantId);

    @Query("SELECT * FROM tasks WHERE plantId = :plantId AND completed = 0 ORDER BY dueDate ASC")
    List<Task> getTasksForPlantSync(String plantId);

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    LiveData<List<Task>> getPendingTasks();

    @Query("SELECT * FROM tasks WHERE completed = 0 ORDER BY dueDate ASC")
    List<Task> getPendingTasksSync();

    @Insert
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Query("DELETE FROM tasks WHERE plantId = :plantId")
    void deleteAllForPlant(String plantId);
}
