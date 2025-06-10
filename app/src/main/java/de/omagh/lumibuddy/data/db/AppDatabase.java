package de.omagh.lumibuddy.data.db;

import androidx.room.RoomDatabase;
import androidx.room.Database;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryEntry; // ✅ Correct import

@Database(
        entities = {
                Plant.class,
                DiaryEntry.class // ✅ Add this line
        },
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlantDao plantDao();

    public abstract DiaryDao diaryDao();
}
