package de.omagh.feature_diary.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_diary.ui.DiaryViewModel;

@Component(
        dependencies = CoreComponent.class,
        modules = DiaryModule.class
)
public interface DiaryComponent {
    void inject(DiaryViewModel vm);

    @Component.Factory
    interface Factory {
        DiaryComponent create(CoreComponent core);
    }
}