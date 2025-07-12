package de.omagh.feature_growschedule.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Factory for HomeViewModel backed by Dagger provider.
 */
public class HomeViewModelFactory implements ViewModelProvider.Factory {
    private final Provider<HomeViewModel> provider;

    @Inject
    public HomeViewModelFactory(Provider<HomeViewModel> provider) {
        this.provider = provider;
    }

    @SuppressWarnings("unchecked")
    @Override
    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HomeViewModel.class)) {
            return (T) provider.get();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
