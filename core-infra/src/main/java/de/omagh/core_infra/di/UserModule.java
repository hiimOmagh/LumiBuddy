package de.omagh.core_infra.di;

import android.app.Application;

import dagger.Module;
import dagger.Provides;

import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.core_infra.user.UserProfileSyncManager;
import de.omagh.core_infra.user.LightCorrectionStore;
import de.omagh.core_infra.firebase.FirebaseManager;

/**
 * Provides user/profile related managers.
 */
@Module
public class UserModule {
    @Provides
    SettingsManager provideSettingsManager(Application app) {
        return new SettingsManager(app.getApplicationContext());
    }

    @Provides
    CalibrationProfilesManager provideCalibrationProfilesManager(Application app) {
        return new CalibrationProfilesManager(app.getApplicationContext());
    }

    @Provides
    UserProfileManager provideUserProfileManager(Application app) {
        return new UserProfileManager(app.getApplicationContext());
    }

    @Provides
    UserProfileSyncManager provideUserProfileSyncManager(Application app) {
        return new UserProfileSyncManager(app.getApplicationContext());
    }

    @Provides
    FirebaseManager provideFirebaseManager() {
        return new FirebaseManager();
    }

    @Provides
    LightCorrectionStore provideLightCorrectionStore(Application app) {
        return new LightCorrectionStore(app.getApplicationContext());
    }
}
