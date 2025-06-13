package de.omagh.lumibuddy.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import de.omagh.lumibuddy.data.model.GrowLightProduct;

/**
 * DAO for caching grow light product information.
 */
@Dao
public interface GrowLightProductDao {
    @Query("SELECT * FROM grow_light_products WHERE brand LIKE :query OR model LIKE :query")
    List<GrowLightProduct> search(String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<GrowLightProduct> products);
}