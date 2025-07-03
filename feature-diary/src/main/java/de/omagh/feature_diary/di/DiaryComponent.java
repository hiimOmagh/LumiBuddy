package de.omagh.feature_diary.di;

import dagger.Subcomponent;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_diary.ui.DiaryViewModel;

@Subcomponent(modules = DiaryModule.class)
public interface DiaryComponent {
    void inject(DiaryViewModel viewModel);

    @Subcomponent.Factory
    interface Factory {
        DiaryComponent create();
    }
}