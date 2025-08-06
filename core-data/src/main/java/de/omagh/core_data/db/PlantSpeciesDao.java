package de.omagh.core_data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.omagh.core_data.model.PlantSpeciesEntity;

/**
 * DAO for plant species cached locally.
 */
@Dao
public interface PlantSpeciesDao {
    @Query("SELECT * FROM PlantSpeciesEntity WHERE commonName LIKE :query OR scientificName LIKE :query")
    List<PlantSpeciesEntity> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlantSpeciesEntity> species);
}