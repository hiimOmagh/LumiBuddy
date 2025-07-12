package de.omagh.feature_measurement;

import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;

import de.omagh.core_data.db.GrowLightProductDao;
import de.omagh.core_data.model.GrowLightProduct;
import de.omagh.feature_measurement.infra.GrowLightProductRepository;
import de.omagh.core_infra.network.GrowLightApiService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class GrowLightProductRepositoryTest {
    @Mock
    GrowLightApiService apiService;
    @Mock
    GrowLightProductDao productDao;
    @Mock
    Call<List<GrowLightProduct>> call;

    private GrowLightProductRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        repository = new GrowLightProductRepository(apiService, productDao, executor);
    }

    @Test
    public void searchGrowLights_returnsApiResults() throws Exception {
        List<GrowLightProduct> list = Collections.singletonList(new GrowLightProduct("1", "B", "M", "", 10, 0f, "", "", ""));
        Response<List<GrowLightProduct>> resp = Response.success(list);
        Mockito.when(apiService.searchLamps(Mockito.eq("led"), Mockito.anyString())).thenReturn(call);
        Mockito.when(call.execute()).thenReturn(resp);
        Mockito.when(productDao.search("%led%"))
                .thenReturn(Collections.emptyList());

        LiveData<List<GrowLightProduct>> live = repository.searchGrowLights("led");
        List<GrowLightProduct> result = getValue(live);

        Mockito.verify(productDao).insertAll(list);
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId());
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
