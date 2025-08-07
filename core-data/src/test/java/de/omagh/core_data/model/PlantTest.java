package de.omagh.core_data.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class PlantTest {
    @Test
    public void constructor_setsFields() {
        Plant plant = new Plant("id1", "Rose", "Flower", "img://rose", 12345L);
        assertEquals("id1", plant.getId());
        assertEquals("Rose", plant.getName());
        assertEquals("Flower", plant.getType());
        assertEquals("img://rose", plant.getImageUri());
        assertEquals(12345L, plant.getUpdatedAt());
    }
}
