package de.omagh.feature_plantdb.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.omagh.core_infra.user.UserProfileManager;

/**
 * ViewModel mediating access to {@link UserProfileManager}.
 * Exposes profile data as LiveData for the ProfileFragment.
 */
public class ProfileViewModel extends AndroidViewModel {
    private final UserProfileManager manager;
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> avatarUri = new MutableLiveData<>();
    private final MutableLiveData<String> theme = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        this(application, new UserProfileManager(application.getApplicationContext()));
    }

    // Constructor for tests
    public ProfileViewModel(@NonNull Application application, UserProfileManager manager) {
        super(application);
        this.manager = manager;
        loadFromManager();
    }

    private void loadFromManager() {
        username.setValue(manager.getUsername());
        avatarUri.setValue(manager.getAvatarUri());
        theme.setValue(manager.getTheme());
    }

    /**
     * Returns the username LiveData.
     */
    public LiveData<String> getUsername() {
        return username;
    }

    /**
     * Update the username and persist it.
     */
    public void setUsername(String name) {
        manager.setUsername(name);
        username.setValue(name);
    }

    /**
     * Returns the avatar URI LiveData.
     */
    public LiveData<String> getAvatarUri() {
        return avatarUri;
    }

    /**
     * Update the avatar URI and persist it.
     */
    public void setAvatarUri(String uri) {
        manager.setAvatarUri(uri);
        avatarUri.setValue(uri);
    }

    /**
     * Returns the theme LiveData.
     */
    public LiveData<String> getTheme() {
        return theme;
    }

    /**
     * Update the theme name and persist it.
     */
    public void setTheme(String value) {
        manager.setTheme(value);
        theme.setValue(value);
    }

    /**
     * Trigger manual sync to the cloud. Currently a no-op.
     */
    public void syncToCloud() {
        manager.syncToCloud();
    }
}