package de.omagh.lumibuddy.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> welcomeText = new MutableLiveData<>("Welcome to LumiBuddy!");

    public LiveData<String> getWelcomeText() {
        return welcomeText;
    }

    // You can add more LiveData for stats, notifications, etc.
}
