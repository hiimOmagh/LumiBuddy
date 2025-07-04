package de.omagh.feature_measurement.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.feature_measurement.infra.GrowLightProductRepository;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;

/**
 * ViewModel for lamp profile list management.
 */
public class LampProfilesViewModel extends AndroidViewModel {
    private final MutableLiveData<List<LampProduct>> profiles = new MutableLiveData<>();
    private final GrowLightProfileManager manager;
    private final GrowLightProductRepository productRepository;

    public LampProfilesViewModel(@NonNull Application application) {
        super(application);
        manager = new GrowLightProfileManager(application.getApplicationContext());
        productRepository = new GrowLightProductRepository(application.getApplicationContext());
        profiles.setValue(manager.getAllProfiles());
    }

    public LiveData<List<LampProduct>> getProfiles() {
        return profiles;
    }

    public void addProfile(LampProduct p) {
        manager.addCustomProfile(p);
        profiles.setValue(manager.getAllProfiles());
    }

    public void updateProfile(LampProduct p) {
        manager.updateProfile(p);
        profiles.setValue(manager.getAllProfiles());
    }

    public void removeProfile(LampProduct p) {
        manager.removeCustomProfile(p.id);
        profiles.setValue(manager.getAllProfiles());
    }

    public void selectProfile(LampProduct p) {
        manager.setActiveLampProfile(p.id);
    }

    public LiveData<List<GrowLightProduct>> searchProducts(String query) {
        return productRepository.searchGrowLights(query);
    }
}