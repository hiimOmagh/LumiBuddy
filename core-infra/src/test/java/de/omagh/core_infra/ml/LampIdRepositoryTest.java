package de.omagh.core_infra.ml;

import static org.junit.Assert.*;

import android.graphics.Bitmap;
import android.util.Base64;

import androidx.lifecycle.LiveData;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import de.omagh.core_infra.network.lampid.LampIdRequest;
import de.omagh.core_infra.network.lampid.LampIdResponse;
import de.omagh.core_infra.network.lampid.LampIdService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LampIdRepositoryTest {
    @Mock
    LampIdService service;
    @Mock
    Call<LampIdResponse> call;

    private LampIdRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        repository = new LampIdRepository(service, executor);
    }

    @Test
    public void identifyLamp_buildsRequestFromBitmap() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
        String expected = Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP);

        LampIdResponse resp = new LampIdResponse();
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<LampIdResponse> cb = invocation.getArgument(0);
            cb.onResponse(call, Response.success(resp));
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<String> live = repository.identifyLamp(bmp);
        getValue(live);

        ArgumentCaptor<LampIdRequest> captor = ArgumentCaptor.forClass(LampIdRequest.class);
        Mockito.verify(service).identify(captor.capture());
        assertEquals(expected, captor.getValue().getImage());
    }

    @Test
    public void identifyLamp_mapsResponse() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        LampIdResponse resp = new LampIdResponse();
        java.lang.reflect.Field f = LampIdResponse.class.getDeclaredField("label");
        f.setAccessible(true);
        f.set(resp, "LED Lamp");
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<LampIdResponse> cb = invocation.getArgument(0);
            cb.onResponse(call, Response.success(resp));
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<String> live = repository.identifyLamp(bmp);
        String result = getValue(live);

        assertEquals("LED Lamp", result);
    }

    @Test
    public void identifyLamp_failurePostsNull() throws Exception {
        Bitmap bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        Mockito.when(service.identify(Mockito.any())).thenReturn(call);
        Mockito.doAnswer(invocation -> {
            Callback<LampIdResponse> cb = invocation.getArgument(0);
            cb.onFailure(call, new RuntimeException());
            return null;
        }).when(call).enqueue(Mockito.any());

        LiveData<String> live = repository.identifyLamp(bmp);
        String result = getValue(live);

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
