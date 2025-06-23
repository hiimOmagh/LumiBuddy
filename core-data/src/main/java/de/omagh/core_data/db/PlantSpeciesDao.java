package de.omagh.core_data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.omagh.core_data.model.PlantSpecies;

/**
 * DAO for plant species cached locally.
 */
@Dao
public interface PlantSpeciesDao {
    @Query("SELECT * FROM plant_species WHERE commonName LIKE :query OR scientificName LIKE :query")
    List<PlantSpecies> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlantSpecies> species);
}