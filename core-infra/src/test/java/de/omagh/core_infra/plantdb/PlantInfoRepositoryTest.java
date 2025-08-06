package de.omagh.core_infra.plantdb;

import static org.junit.Assert.*;

import androidx.lifecycle.LiveData;

import de.omagh.core_data.db.PlantCareProfileDao;
import de.omagh.core_data.db.PlantSpeciesDao;
import de.omagh.core_data.model.PlantSpeciesEntity;
import de.omagh.core_domain.model.PlantSpecies;
import de.omagh.core_infra.network.PlantApiService;
import de.omagh.core_infra.network.PlantIdApiService;

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

public class PlantInfoRepositoryTest {
    @Mock
    PlantApiService apiService;
    @Mock
    PlantIdApiService idService;
    @Mock
    PlantSpeciesDao speciesDao;
    @Mock
    PlantCareProfileDao profileDao;
    @Mock
    Call<List<PlantSpeciesEntity>> speciesCall;

    private PlantInfoRepository repository;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        repository = new PlantInfoRepository(apiService, idService, speciesDao, profileDao, executor);
    }

    @Test
    public void searchSpecies_returnsApiResultsAndCaches() throws Exception {
        List<PlantSpeciesEntity> list = Collections.singletonList(new PlantSpeciesEntity("1", "Sci", "Com", ""));
        Response<List<PlantSpeciesEntity>> resp = Response.success(list);
        Mockito.when(apiService.searchSpecies(Mockito.eq("tom"), Mockito.anyString())).thenReturn(speciesCall);
        Mockito.when(speciesCall.execute()).thenReturn(resp);
        Mockito.when(speciesDao.search("%tom%"))
                .thenReturn(Collections.emptyList());

        LiveData<List<PlantSpeciesEntity>> live = repository.searchSpecies("tom");
        List<PlantSpeciesEntity> result = getValue(live);

        Mockito.verify(speciesDao).insertAll(list);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Sci", result.get(0).getScientificName());
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
