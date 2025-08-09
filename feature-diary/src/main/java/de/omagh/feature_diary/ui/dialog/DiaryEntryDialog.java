package de.omagh.feature_diary.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.feature_diary.R;

import java.util.UUID;

/**
 * Utility helper to create diary entry dialogs.
 */
public class DiaryEntryDialog {
    public static void show(Context context, OnEntryAddedListener listener) {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_diary_entry_global, null);
        Spinner plantSpinner = dialog.findViewById(R.id.plantSpinner);
        Spinner typeSpinner = dialog.findViewById(R.id.eventTypeSpinner);
        EditText noteInput = dialog.findViewById(R.id.editDiaryNote);
        ImageView imagePreview = dialog.findViewById(R.id.diaryImagePreview);

        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.add_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(android.R.string.cancel, null)
 .create();

        alert.setOnShowListener(d -> alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String note = noteInput.getText().toString().trim();
            String eventType = typeSpinner.getSelectedItem() != null ? typeSpinner.getSelectedItem().toString() : "";
            if (note.isEmpty() || eventType.isEmpty()) {
                Toast.makeText(context, R.string.diary_entry_validation_error, Toast.LENGTH_SHORT).show();
                return;
            }
            DiaryEntry entry = new DiaryEntry(
                    UUID.randomUUID().toString(),
                    "",
                    System.currentTimeMillis(),
                    note,
                    "",
                    eventType
            );
            listener.onEntryAdded(entry);
            alert.dismiss();
        }));
        alert.show();
    }

    public static void edit(Context context, DiaryEntry entry, OnEntryEditedListener listener) {
        View dialog = LayoutInflater.from(context).inflate(R.layout.dialog_add_diary_entry, null);
        Spinner typeSpinner = dialog.findViewById(R.id.eventTypeSpinner);
        EditText noteInput = dialog.findViewById(R.id.editDiaryNote);
        noteInput.setText(entry.getNote());
        String[] types = context.getResources().getStringArray(R.array.diary_event_types);
        for (int i = 0; i < types.length; i++) {
            if (types[i].equals(entry.getEventType())) {
                typeSpinner.setSelection(i);
                break;
            }
        }

        AlertDialog alert = new AlertDialog.Builder(context)
                .setTitle(R.string.edit_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(android.R.string.cancel, null)
 .create();

        alert.setOnShowListener(d -> alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String note = noteInput.getText().toString().trim();
            String eventType = typeSpinner.getSelectedItem() != null ? typeSpinner.getSelectedItem().toString() : "";
            if (note.isEmpty() || eventType.isEmpty()) {
                Toast.makeText(context, R.string.diary_entry_validation_error, Toast.LENGTH_SHORT).show();
                return;
            }
            DiaryEntry edited = new DiaryEntry(
                    entry.getId(),
                    entry.getPlantId(),
                    System.currentTimeMillis(),
                    note,
                    entry.getImageUri(),
                    eventType
            );
            listener.onEntryEdited(edited);
            alert.dismiss();
        }));
        alert.show();
    }

    public interface OnEntryAddedListener {
        void onEntryAdded(DiaryEntry entry);
    }

    public interface OnEntryEditedListener {
        void onEntryEdited(DiaryEntry entry);
    }
}
