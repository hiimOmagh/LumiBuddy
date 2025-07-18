package de.omagh.feature_diary.di;

/**
 * Dagger component for the Diary feature. Depends on
 * {@link de.omagh.core_infra.di.CoreComponent} and installs
 * {@link DiaryModule} to supply database and repository bindings.
 */

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_diary.ui.DiaryViewModel;
import de.omagh.feature_diary.ui.TaskViewModel;

@FeatureScope
@Component(
        dependencies = CoreComponent.class,
        modules = DiaryModule.class
)
public interface DiaryComponent {
    void inject(DiaryViewModel vm);

    void inject(TaskViewModel vm);

    @Component.Factory
    interface Factory {
        DiaryComponent create(CoreComponent core);
    }
}