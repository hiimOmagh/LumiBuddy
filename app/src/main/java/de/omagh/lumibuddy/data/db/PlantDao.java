package de.omagh.lumibuddy.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import de.omagh.lumibuddy.data.model.Plant;

import java.util.List;

@Dao
public interface PlantDao {
    @Query("SELECT * FROM plants")
    LiveData<List<Plant>> getAll();

    @Insert
    void insert(Plant plant);

    @androidx.room.Delete
    void delete(Plant plant);

    @androidx.room.Update
    void update(Plant plant);

}


