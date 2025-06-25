package de.omagh.app_di;

import de.omagh.feature_measurement.ui.MeasureViewModel;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.feature_plantdb.ui.PlantListViewModel;

/**
 * Interface that exposes injection targets used by feature modules.
 * Implemented by application level Dagger component.
 */
public interface ApplicationComponent {
    void inject(MeasureViewModel viewModel);

    void inject(PlantDetailViewModel viewModel);

    void inject(PlantListViewModel viewModel);
}