
package de.omagh.lumibuddy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> welcomeText = new MutableLiveData<>("Welcome to LumiBuddy!");

    // Mock metrics for dashboard preview
    private final MutableLiveData<Float> lux = new MutableLiveData<>(650f);
    private final MutableLiveData<Float> ppfd = new MutableLiveData<>(35f);
    private final MutableLiveData<Float> dli = new MutableLiveData<>(12.0f);

    private final MutableLiveData<java.util.List<String>> tasks =
            new MutableLiveData<>(java.util.Arrays.asList("Water Basil today", "Fertilize Orchids tomorrow"));

    public LiveData<String> getWelcomeText() {
        return welcomeText;
    }

    public LiveData<Float> getLux() {
        return lux;
    }

    public LiveData<Float> getPpfd() {
        return ppfd;
    }

    public LiveData<Float> getDli() {
        return dli;
    }

    public LiveData<java.util.List<String>> getTasks() {
        return tasks;
    }

    // You can add more LiveData for stats, notifications, etc.
}