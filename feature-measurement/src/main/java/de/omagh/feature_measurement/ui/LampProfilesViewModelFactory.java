package de.omagh.feature_measurement.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Dagger-backed factory for {@link LampProfilesViewModel}.
 */
public class LampProfilesViewModelFactory implements ViewModelProvider.Factory {
    private final Provider<LampProfilesViewModel> provider;

    @Inject
    public LampProfilesViewModelFactory(Provider<LampProfilesViewModel> provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LampProfilesViewModel.class)) {
            return (T) provider.get();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
