package de.omagh.core_data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.core_data.model.GrowLightProfile;
import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import de.omagh.core_data.model.Plant;
import de.omagh.core_data.model.Task;
import de.omagh.core_data.model.LightCorrectionEntity;

@Database(
        entities = {
                Plant.class,
                DiaryEntry.class,
                GrowLightProfile.class,
                PlantSpecies.class,
                PlantCareProfileEntity.class,
                GrowLightProduct.class,
                Task.class,
                LightCorrectionEntity.class
        },
        version = 8,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static final Migration MIGRATION_4_5 =
            new Migration(4, 5) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    // No schema changes. Potential place to migrate stored image URIs
                    // to internal storage if needed in future versions.
                }
            };
    private static volatile AppDatabase instance;

    /**
     * Returns the singleton instance of the database.
     */
    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "lumibuddy.db")
                            .addMigrations(MIGRATION_4_5)
                            .fallbackToDestructiveMigration() // Only destructive migration on upgrade
                            .build();
                }
            }
        }
        return instance;
    }

    public abstract PlantDao plantDao();

    public abstract DiaryDao diaryDao();

    public abstract GrowLightDao growLightDao();

    public abstract PlantSpeciesDao plantSpeciesDao();

    public abstract PlantCareProfileDao plantCareProfileDao();

    public abstract GrowLightProductDao growLightProductDao();

    public abstract TaskDao taskDao();

    public abstract LightCorrectionDao lightCorrectionDao();
}
