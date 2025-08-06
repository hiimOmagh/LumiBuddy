package de.omagh.core_infra.ml;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.network.RetrofitClient;
import de.omagh.core_infra.network.lampid.LampIdRequest;
import de.omagh.core_infra.network.lampid.LampIdResponse;
import de.omagh.core_infra.network.lampid.LampIdService;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Placeholder repository for future cloud based lamp identification.
 * Currently returns {@code null} predictions but can be extended to call
 * a remote API or dataset similar to {@link de.omagh.core_infra.plantdb.PlantIdRepository}.
 * Contributors may replace this with a network backed implementation.
 */
public class LampIdRepository {
    private final LampIdService service;
    private final ExecutorService executor;

    public LampIdRepository(AppExecutors executors) {
        this(RetrofitClient.getInstance().create(LampIdService.class), executors.single());
    }

    // Constructor for tests
    public LampIdRepository(LampIdService service, ExecutorService executor) {
        this.service = service;
        this.executor = executor;
    }

    /**
     * Identify the lamp in the given bitmap via a remote service.
     */
    public LiveData<String> identifyLamp(Bitmap bitmap) {
        MutableLiveData<String> result = new MutableLiveData<>();
        executor.execute(() -> {
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                String base64 = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);
                LampIdRequest request = new LampIdRequest(base64);
                Call<LampIdResponse> call = service.identify(request);
                Response<LampIdResponse> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    result.postValue(resp.body().getLabel());
                    return;
                }
            } catch (Exception ignored) {
            }
            result.postValue(null);
        });
        return result;
    }
}
