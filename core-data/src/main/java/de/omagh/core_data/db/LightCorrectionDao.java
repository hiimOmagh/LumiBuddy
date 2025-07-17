package de.omagh.core_data.db;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import de.omagh.core_data.model.LightCorrectionEntity;

/**
 * DAO for light type correction factors.
 */
@Dao
public interface LightCorrectionDao {
    @Query("SELECT factor FROM light_corrections WHERE type = :type")
    @Nullable
    float getFactor(String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LightCorrectionEntity entity);
}
