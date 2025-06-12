package de.omagh.lumibuddy.network;

import java.util.List;

import de.omagh.lumibuddy.feature_diary.DiaryEntry;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Retrofit interface for syncing diary entries with a remote backend.
 */
public interface DiarySyncService {

    /**
     * Upload diary entries to the cloud.
     */
    @POST("sync/diary")
    Call<Void> uploadEntries(@Body List<DiaryEntry> entries);

    /**
     * Download diary entries from the cloud.
     */
    @GET("sync/diary")
    Call<List<DiaryEntry>> downloadEntries();
}
