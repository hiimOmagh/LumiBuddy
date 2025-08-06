package de.omagh.core_infra.plantdb;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.omagh.core_infra.network.plantid.PlantIdRequest;
import de.omagh.core_infra.network.plantid.PlantIdResponse;
import de.omagh.core_infra.network.plantid.PlantIdService;
import de.omagh.core_infra.network.plantid.PlantIdSuggestion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlantIdRepositoryTest {
    @Mock
    PlantIdService service;
    @Mock
    Call<PlantIdResponse> call;

    private PlantIdRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        repository = new PlantIdRepository(service, executor);
    }

    @Test
    public void identifyPlant_buildsRequestFromBitmap() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        String expected = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);

        PlantIdResponse resp = new PlantIdResponse();
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<PlantIdResponse> cb = invocation.getArgument(0);
            cb.onResponse(call, Response.success(resp));
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<PlantIdSuggestion> live = repository.identifyPlant(bmp);
        getValue(live);

        ArgumentCaptor<PlantIdRequest> captor = ArgumentCaptor.forClass(PlantIdRequest.class);
        Mockito.verify(service).identify(captor.capture());
        assertEquals(Collections.singletonList(expected), captor.getValue().getImages());
    }

    @Test
    public void identifyPlant_mapsResponse() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        String json = "{\"suggestions\":[{\"name\":\"Solanum lycopersicum\",\"details\":{\"common_names\":[\"Tomato\"]}}]}";
        PlantIdResponse resp = new Gson().fromJson(json, PlantIdResponse.class);
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<PlantIdResponse> cb = invocation.getArgument(0);
            cb.onResponse(call, Response.success(resp));
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<PlantIdSuggestion> live = repository.identifyPlant(bmp);
        PlantIdSuggestion result = getValue(live);

        assertNotNull(result);
        assertEquals("Tomato", result.getCommonName());
        assertEquals("Solanum lycopersicum", result.getScientificName());
    }

    @Test
    public void identifyPlant_failurePostsNull() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<PlantIdResponse> cb = invocation.getArgument(0);
            cb.onFailure(call, new RuntimeException());
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<PlantIdSuggestion> live = repository.identifyPlant(bmp);
        PlantIdSuggestion result = getValue(live);

        assertNull(result);
    }

    private <T> T getValue(LiveData<T> live) throws InterruptedException {
        final Object[] data = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        live.observeForever(t -> {
            data[0] = t;
            latch.countDown();
        });
        latch.await(2, TimeUnit.SECONDS);
        //noinspection unchecked
        return (T) data[0];
    }
}
