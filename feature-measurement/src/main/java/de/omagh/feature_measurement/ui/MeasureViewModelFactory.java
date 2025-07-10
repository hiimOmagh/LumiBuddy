package de.omagh.feature_measurement.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Simple Dagger-backed factory for {@link MeasureViewModel}.
 */
public class MeasureViewModelFactory implements ViewModelProvider.Factory {
    private final Provider<MeasureViewModel> provider;

    @Inject
    public MeasureViewModelFactory(Provider<MeasureViewModel> provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MeasureViewModel.class)) {
            return (T) provider.get();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}