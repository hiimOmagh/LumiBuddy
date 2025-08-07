package de.omagh.feature_diary.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;

import java.util.List;

import javax.inject.Inject;

import de.omagh.core_data.model.Task;
import de.omagh.core_data.repository.TaskRepository;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.feature_diary.di.DaggerDiaryComponent;
import de.omagh.feature_diary.di.DiaryComponent;

/**
 * ViewModel for managing pending tasks for a specific plant.
 */
public class TaskViewModel extends AndroidViewModel {

    @Inject
    TaskRepository repository;

    private final MutableLiveData<List<Task>> tasks = new MutableLiveData<>();

    public TaskViewModel(@NonNull Application application) {
        this(application, null);
    }

    // Constructor for tests allowing repository injection
    public TaskViewModel(@NonNull Application application, TaskRepository repository) {
        super(application);
        if (repository != null) {
            this.repository = repository;
        } else {
            CoreComponent core = ((CoreComponentProvider) application).getCoreComponent();
            DiaryComponent component = DaggerDiaryComponent.factory().create(core);
            component.inject(this);
        }
    }

    public LiveData<List<Task>> getPendingTasks(String plantId) {
        return repository.getTasksForPlant(plantId);
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public void loadTasks() {
        tasks.setValue(repository.getPendingTasksSync());
    }

    public void completeTask(Task task) {
        Task completed = new Task(task.getId(), task.getPlantId(), task.getDescription(),
                task.getDueDate(), true);
        repository.update(completed);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.shutdown();
    }

    /**
     * Factory to inject dependencies into the ViewModel.
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final Application application;

        public Factory(Application application) {
            this.application = application;
        }

        @androidx.annotation.NonNull
        @SuppressWarnings("unchecked")
        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass.isAssignableFrom(TaskViewModel.class)) {
                return (T) new TaskViewModel(application, null);
            }
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
