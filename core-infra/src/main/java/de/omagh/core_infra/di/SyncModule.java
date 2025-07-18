package de.omagh.core_infra.di;

import dagger.Module;
import dagger.Provides;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.firebase.FirebaseManager;
import de.omagh.core_infra.sync.PlantSyncManager;
import de.omagh.core_infra.sync.DiarySyncManager;
import de.omagh.core_infra.user.SettingsManager;

/**
 * Provides managers that handle Firestore synchronisation.
 */
@Module
public class SyncModule {
    @Provides
    PlantSyncManager providePlantSyncManager(PlantRepository localRepo,
                                             FirebaseManager firebaseManager,
                                             SettingsManager settingsManager,
                                             AppExecutors executors) {
        return new PlantSyncManager(localRepo, firebaseManager, settingsManager, executors);
    }

    @Provides
    DiarySyncManager provideDiarySyncManager(DiaryRepository localRepo,
                                             FirebaseManager firebaseManager,
                                             SettingsManager settingsManager,
                                             AppExecutors executors) {
        return new DiarySyncManager(localRepo, firebaseManager, settingsManager, executors);
    }
}
