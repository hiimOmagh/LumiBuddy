package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Log entry associated with a particular plant.
 * <p>
 * Entries record events such as watering or light measurements along with
 * optional notes and images. They are stored in the {@code diary_entries} table
 * with {@code timestamp} describing when the event occurred.
 */
@Entity(tableName = "diary_entries")
public class DiaryEntry {

    @PrimaryKey
    @NonNull
    private final String id;

    private final String plantId;      // Foreign key reference
    private final long timestamp;      // Unix time
    private final String note;         // Optional text note
    private final String imageUri;     // Optional image URI
    private final String eventType;    // e.g., "watering", "note", "light"

    public DiaryEntry(@NonNull String id, String plantId, long timestamp, String note, String imageUri, String eventType) {
        this.id = id;
        this.plantId = plantId;
        this.timestamp = timestamp;
        this.note = note;
        this.imageUri = imageUri;
        this.eventType = eventType;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getPlantId() {
        return plantId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getNote() {
        return note;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getEventType() {
        return eventType;
    }
}
