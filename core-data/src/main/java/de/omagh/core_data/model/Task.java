package de.omagh.core_data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey
    @NonNull
    private final String id;
    private final String plantId;
    private final String description;
    private final long dueDate;
    private final boolean completed;

    public Task(@NonNull String id, String plantId, String description,
                long dueDate, boolean completed) {
        this.id = id;
        this.plantId = plantId;
        this.description = description;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getPlantId() {
        return plantId;
    }

    public String getDescription() {
        return description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }
}
