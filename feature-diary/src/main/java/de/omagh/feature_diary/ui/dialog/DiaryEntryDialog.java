package de.omagh.feature_diary.ui.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

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

        new AlertDialog.Builder(context)
                .setTitle(R.string.add_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, (d, which) -> {
                    DiaryEntry entry = new DiaryEntry(
                            UUID.randomUUID().toString(),
                            "",
                            System.currentTimeMillis(),
                            noteInput.getText().toString().trim(),
                            "",
                            typeSpinner.getSelectedItem().toString()
                    );
                    listener.onEntryAdded(entry);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
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

        new AlertDialog.Builder(context)
                .setTitle(R.string.edit_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, (d, w) -> {
                    DiaryEntry edited = new DiaryEntry(
                            entry.getId(),
                            entry.getPlantId(),
                            System.currentTimeMillis(),
                            noteInput.getText().toString().trim(),
                            entry.getImageUri(),
                            typeSpinner.getSelectedItem().toString()
                    );
                    listener.onEntryEdited(edited);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    public interface OnEntryAddedListener {
        void onEntryAdded(DiaryEntry entry);
    }

    public interface OnEntryEditedListener {
        void onEntryEdited(DiaryEntry entry);
    }
}