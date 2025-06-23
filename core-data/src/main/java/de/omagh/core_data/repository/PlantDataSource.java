package de.omagh.core_data.repository;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.omagh.core_domain.model.Plant;

/**
 * Abstraction for plant data operations used by ViewModels.
 */
public interface PlantDataSource {
    LiveData<List<Plant>> getAllPlants();

    LiveData<Plant> getPlant(String id);

    void insertPlant(Plant plant);

    void updatePlant(Plant plant);

    void deletePlant(Plant plant);
}