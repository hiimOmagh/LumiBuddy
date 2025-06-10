package de.omagh.lumibuddy.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import de.omagh.lumibuddy.feature_diary.DiaryEntry;

@Dao
public interface DiaryDao {

    @Query("SELECT * FROM diary_entries WHERE plantId = :plantId ORDER BY timestamp DESC")
    LiveData<List<DiaryEntry>> getEntriesForPlant(String plantId);

    @Insert
    void insert(DiaryEntry entry);

    @Update
    void update(DiaryEntry entry);

    @Delete
    void delete(DiaryEntry entry);

    @Query("DELETE FROM diary_entries WHERE plantId = :plantId")
    void deleteAllForPlant(String plantId);
}
