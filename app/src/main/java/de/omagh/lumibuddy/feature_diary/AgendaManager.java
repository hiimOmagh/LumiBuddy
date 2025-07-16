package de.omagh.lumibuddy.feature_diary;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.TimeZone;

import de.omagh.core_data.model.Task;
import de.omagh.core_data.repository.TaskRepository;

/**
 * Coordinates scheduling of {@link Task} items and optionally records them in
 * the device calendar. This is a very small wrapper around
 * {@link TaskRepository} but keeps calendar integration in one place.
 */
@SuppressWarnings("unused")
public class AgendaManager {
    private final Context context;
    private final TaskRepository repository;

    public AgendaManager(Context context, TaskRepository repository) {
        this.context = context.getApplicationContext();
        this.repository = repository;
    }

    /**
     * Returns all incomplete tasks for the given plant as a LiveData stream.
     */
    public LiveData<List<Task>> getTasksForPlant(String plantId) {
        return repository.getTasksForPlant(plantId);
    }

    /**
     * LiveData of all pending tasks across plants.
     */
    public LiveData<List<Task>> getPendingTasks() {
        return repository.getPendingTasks();
    }

    /**
     * Inserts a new task and optionally creates a calendar event so the user can
     * view it in their normal calendar app.
     */
    public void addTask(Task task, boolean addToCalendar) {
        repository.insert(task);
        if (addToCalendar) {
            addEventToCalendar(task);
        }
    }

    /**
     * Updates a task in the repository.
     */
    public void updateTask(Task task) {
        repository.update(task);
    }

    /**
     * Removes a task from the repository.
     */
    public void deleteTask(Task task) {
        repository.delete(task);
    }

    private void addEventToCalendar(@Nullable Task task) {
        if (task == null) return;
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, task.getDueDate());
            values.put(CalendarContract.Events.DTEND, task.getDueDate());
            values.put(CalendarContract.Events.TITLE, task.getDescription());
            values.put(CalendarContract.Events.CALENDAR_ID, 1);
            values.put(CalendarContract.Events.EVENT_TIMEZONE,
                    TimeZone.getDefault().getID());
            resolver.insert(CalendarContract.Events.CONTENT_URI, values);
        } catch (Exception ignored) {
            // Calendar access is best-effort only
        }
    }
}
