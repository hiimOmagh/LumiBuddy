package de.omagh.core_infra.ml;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * Placeholder repository for future cloud based lamp identification.
 * Currently returns {@code null} predictions but can be extended to call
 * a remote API or dataset similar to {@link de.omagh.core_infra.plantdb.PlantIdRepository}.
 */
public class LampIdRepository {
    public LiveData<String> identifyLamp(Bitmap bitmap) {
        MutableLiveData<String> result = new MutableLiveData<>();
        // TODO connect to real lamp recognition service
        result.postValue(null);
        return result;
    }
}
