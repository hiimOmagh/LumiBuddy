package de.omagh.lumibuddy.feature_user;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Basic user profile storage. Only keeps a name and avatar URI for now.
 * This can later be replaced by a Room implementation via {@link de.omagh.lumibuddy.data.db.UserDao}.
 */
public class UserProfileManager {
    private static final String PREFS_NAME = "user_profile";
    private static final String KEY_NAME = "profile_name";
    private static final String KEY_AVATAR = "profile_avatar";

    private final SharedPreferences prefs;

    public UserProfileManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public void setName(String name) {
        prefs.edit().putString(KEY_NAME, name).apply();
    }

    public String getAvatarUri() {
        return prefs.getString(KEY_AVATAR, null);
    }

    public void setAvatarUri(String uri) {
        prefs.edit().putString(KEY_AVATAR, uri).apply();
    }
}