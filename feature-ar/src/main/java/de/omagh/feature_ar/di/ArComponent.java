package de.omagh.feature_ar.di;

/**
 * Dagger component for AR functionality. It links the AR activities to the
 * dependencies provided by {@link de.omagh.core_infra.di.CoreComponent} and
 * bindings in {@link ArModule}.
 */

import dagger.Component;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.FeatureScope;
import de.omagh.feature_ar.ArEntryActivity;
import de.omagh.feature_ar.ArHeatmapActivity;

@FeatureScope
@Component(dependencies = CoreComponent.class, modules = ArModule.class)
public interface ArComponent {
    void inject(ArEntryActivity activity);

    void inject(ArHeatmapActivity activity);

    @Component.Factory
    interface Factory {
        ArComponent create(CoreComponent core);
    }
}
