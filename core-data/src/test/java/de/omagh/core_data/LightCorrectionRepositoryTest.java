package de.omagh.core_data;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.repository.LightCorrectionRepository;

public class LightCorrectionRepositoryTest {
    private AppDatabase db;
    private LightCorrectionRepository repository;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        repository = new LightCorrectionRepository(db.lightCorrectionDao());
    }

    @After
    public void tearDown() {
        db.close();
    }

    @Test
    public void getFactor_returnsOneByDefault() {
        assertEquals(1f, repository.getFactor("LED"), 0f);
    }

    @Test
    public void setFactor_andRetrieve() {
        repository.setFactor("LED", 2f);
        assertEquals(2f, repository.getFactor("LED"), 0f);
    }
}
