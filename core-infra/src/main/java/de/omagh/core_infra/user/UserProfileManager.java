package de.omagh.core_infra.user;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Basic user profile storage. Keeps a username, avatar URI and theme
 * preference. This can later be replaced by a Room implementation via
 * {@link de.omagh.lumibuddy.data.db.UserDao} when cloud sync is added.
 */
public class UserProfileManager {
    private static final String PREFS_NAME = "user_profile";
    /**
     * SharedPreferences key for the stored username.
     */
    private static final String KEY_NAME = "profile_name";
    /**
     * SharedPreferences key for the stored avatar image URI.
     */
    private static final String KEY_AVATAR = "profile_avatar";
    /**
     * SharedPreferences key for the selected UI theme.
     */
    private static final String KEY_THEME = "profile_theme";

    private final SharedPreferences prefs;
    private final Context context;

    public UserProfileManager(Context context) {
        this.context = context.getApplicationContext();
        prefs = this.context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns the stored username.
     */
    public String getUsername() {
        return prefs.getString(KEY_NAME, "");
    }

    /**
     * Persist the given username.
     */
    public void setUsername(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    /**
     * @deprecated use {@link #getUsername()} instead.
     */
    @Deprecated
    public String getName() {
        return getUsername();
    }

    /**
     * @deprecated use {@link #setUsername(String)} instead.
     */
    @Deprecated
    public void setName(String name) {
        setUsername(name);
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR, null);
    }

    public void setAvatarUri(String uri) {
        prefs.edit().putString(KEY_AVATAR, uri).apply();
    }

    /**
     * Returns the chosen UI theme name.
     */
    public String getTheme() {
        return prefs.getString(KEY_THEME, "light");
    }

    /**
     * Persist the desired UI theme name.
     */
    public void setTheme(String theme) {
        prefs.edit().putString(KEY_THEME, theme).apply();
    }

    /**
     * Push the local profile to the cloud using {@link UserProfileSyncManager}.
     */
    public void syncToCloud() {
        new UserProfileSyncManager(context).syncToCloud();
    }

    /**
     * Load the profile from the cloud and merge it locally using
     * {@link UserProfileSyncManager}.
     */
    public void loadFromCloud() {
        new UserProfileSyncManager(context).syncFromCloud();
    }
}