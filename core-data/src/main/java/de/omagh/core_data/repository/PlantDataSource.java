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

    LiveData<Result<Void>> insertPlant(Plant plant);

    LiveData<Result<Void>> updatePlant(Plant plant);

    LiveData<Result<Void>> deletePlant(Plant plant);
}