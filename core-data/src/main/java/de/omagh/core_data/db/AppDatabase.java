package de.omagh.core_data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

//import de.omagh.lumibuddy.data.model.GrowLightProduct;
//import de.omagh.lumibuddy.data.model.GrowLightProfile;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
//import de.omagh.lumibuddy.feature_diary.DiaryEntry;

@Database(
        entities = {
                Plant.class,
                DiaryEntry.class,
                GrowLightProfile.class,
                PlantSpecies.class,
                PlantCareProfileEntity.class,
                GrowLightProduct.class
        },
        version = 5,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;
    private static final Migration MIGRATION_4_5 =
            new Migration(4, 5) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    // No schema changes. Potential place to migrate stored image URIs
                    // to internal storage if needed in future versions.
                }
            };

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
                            .fallbackToDestructiveMigration()
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
}