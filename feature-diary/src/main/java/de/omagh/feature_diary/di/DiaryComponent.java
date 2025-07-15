package de.omagh.feature_diary.di;

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