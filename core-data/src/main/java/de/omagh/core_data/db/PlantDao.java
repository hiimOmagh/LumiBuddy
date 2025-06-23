package de.omagh.core_data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

import de.omagh.core_domain.model.Plant;

@Dao
public interface PlantDao {
    /**
     * Gets all plants from the database as LiveData.
     */
    @Query("SELECT * FROM plants")
    LiveData<List<Plant>> getAll();

    /**
     * Gets a single plant by its ID.
     *
     * @param plantId The unique ID of the plant.
     */
    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    LiveData<Plant> getById(String plantId);

    @Insert
    void insert(Plant plant);

    @Insert
    void insertAll(List<Plant> plants);

    @Update
    void update(Plant plant);

    @Update
    void updateAll(List<Plant> plants);

    @Delete
    void delete(Plant plant);

    @Insert
    long insertAndReturnId(Plant plant);
}



