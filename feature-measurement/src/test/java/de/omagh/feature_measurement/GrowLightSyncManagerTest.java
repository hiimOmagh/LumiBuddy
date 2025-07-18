package de.omagh.feature_measurement;

import static org.mockito.Mockito.*;

import de.omagh.core_data.db.AppDatabase;
import de.omagh.core_data.db.GrowLightDao;
import de.omagh.core_data.model.GrowLightProfile;
import de.omagh.feature_measurement.infra.GrowLightSyncManager;
import de.omagh.core_infra.network.GrowLightApiService;

import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Sample unit test for {@link GrowLightSyncManager}.
 */
public class GrowLightSyncManagerTest {
    @Test
    public void syncLamps_insertsProfilesFromApi() throws Exception {
        AppDatabase db = mock(AppDatabase.class);
        GrowLightDao dao = mock(GrowLightDao.class);
        when(db.growLightDao()).thenReturn(dao);

        GrowLightApiService api = mock(GrowLightApiService.class);
        Call<java.util.List<GrowLightProfile>> call = mock(Call.class);
        java.util.List<GrowLightProfile> list =
                Collections.singletonList(new GrowLightProfile("1", "name", "", "", 0f, "", "", ""));
        when(api.getLamps()).thenReturn(call);
        when(call.execute()).thenReturn(Response.success(list));

        GrowLightSyncManager mgr = new GrowLightSyncManager(db);
        // inject mock api via reflection
        Field f = GrowLightSyncManager.class.getDeclaredField("api");
        f.setAccessible(true);
        f.set(mgr, api);

        mgr.syncLamps();
        Thread.sleep(100); // wait for executor

        verify(dao).insertAll(list);
    }
}
