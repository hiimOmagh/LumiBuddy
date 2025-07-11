package de.omagh.core_data;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.model.Plant;
import de.omagh.core_domain.util.AppExecutors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PlantRepositoryTest {
    private AppDatabase db;
    private PlantRepository repo;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        repo = new PlantRepository(db, new AppExecutors());
    }

    @After
    public void tearDown() throws IOException {
        db.close();
    }

    @Test
    public void insertAndGetPlant() throws Exception {
        Plant plant = new Plant("1", "Rose", "Flower", null);
        repo.insertPlant(plant);
        Plant loaded = repo.getPlant("1").getValue();
        assertEquals("Rose", loaded.getName());
    }
}
