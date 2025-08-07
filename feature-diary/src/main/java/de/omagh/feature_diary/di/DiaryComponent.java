package de.omagh.feature_diary.di;

import dagger.Component;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.repository.TaskRepository;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.core_infra.sync.DiarySyncManager;
import de.omagh.feature_diary.ui.DiaryViewModel;
import de.omagh.feature_diary.ui.TaskViewModel;

/**
 * Dagger component for the Diary feature. Depends on
 * {@link de.omagh.core_infra.di.CoreComponent} and installs
 * {@link DiaryModule} to supply database and repository bindings.
 */
@FeatureScope
@Component(
        dependencies = CoreComponent.class,
        modules = DiaryModule.class
)
public interface DiaryComponent {
    void inject(DiaryViewModel vm);

    void inject(TaskViewModel vm);

    DiaryRepository diaryRepository();

    DiarySyncManager diarySyncManager();

    TaskRepository taskRepository();

    @Component.Factory
    interface Factory {
        DiaryComponent create(CoreComponent core);
    }
}
