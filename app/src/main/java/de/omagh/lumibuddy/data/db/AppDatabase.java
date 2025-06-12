package de.omagh.lumibuddy.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry; // ✅ Correct import
import de.omagh.lumibuddy.data.model.GrowLightProfile;

@Database(
        entities = {
                Plant.class,
                DiaryEntry.class,
                GrowLightProfile.class
        },
        version = 3,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
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
}
