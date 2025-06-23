package de.omagh.lumibuddy.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.omagh.core_domain.model.PlantCareProfileEntity;

/**
 * DAO for plant care profiles cached locally.
 */
@Dao
public interface PlantCareProfileDao {
    @Query("SELECT * FROM plant_care_profiles WHERE speciesId = :speciesId")
    List<PlantCareProfileEntity> getBySpecies(String speciesId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlantCareProfileEntity> profiles);
}