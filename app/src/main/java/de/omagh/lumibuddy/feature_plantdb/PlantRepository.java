package de.omagh.lumibuddy.feature_plantdb;

import androidx.lifecycle.LiveData;

import java.util.List;

import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.data.db.PlantDao;
import de.omagh.lumibuddy.data.model.Plant;

public class PlantRepository {
    private final PlantDao plantDao;

    public PlantRepository(AppDatabase db) {
        this.plantDao = db.plantDao();
    }

    public LiveData<List<Plant>> getAllPlants() {
        return plantDao.getAll();
    }

    public LiveData<Plant> getPlant(long id) {
        return plantDao.getById(id);
    }

    public void insertPlant(Plant plant) {
        AppExecutors.getInstance().diskIO().execute(() ->
                plantDao.insert(plant)
        );
    }

    public void updatePlant(Plant plant) {
        AppExecutors.getInstance().diskIO().execute(() ->
                plantDao.update(plant)
        );
    }

    public void deletePlant(Plant plant) {
        AppExecutors.getInstance().diskIO().execute(() ->
                plantDao.delete(plant)
        );
    }
}

