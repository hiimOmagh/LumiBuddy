package de.omagh.feature_diary.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_diary.ui.DiaryViewModel;

@FeatureScope
@Component(
        dependencies = CoreComponent.class,
        modules = DiaryModule.class
)
public interface DiaryComponent {
    @Component.Factory
    interface Factory {
        DiaryComponent create(CoreComponent core);
    }

    void inject(DiaryViewModel vm);
}