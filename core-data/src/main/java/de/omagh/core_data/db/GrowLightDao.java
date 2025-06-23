package de.omagh.core_data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.omagh.core_data.model.GrowLightProfile;

@Dao
public interface GrowLightDao {
    @Query("SELECT * FROM grow_light_profiles")
    LiveData<List<GrowLightProfile>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GrowLightProfile> profiles);
}