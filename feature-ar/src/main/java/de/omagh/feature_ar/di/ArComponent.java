package de.omagh.feature_ar.di;

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_ar.ArEntryActivity;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = ArModule.class)
public interface ArComponent {
    void inject(ArEntryActivity activity);

    @Component.Factory
    interface Factory {
        ArComponent create(CoreComponent core);
    }
}
