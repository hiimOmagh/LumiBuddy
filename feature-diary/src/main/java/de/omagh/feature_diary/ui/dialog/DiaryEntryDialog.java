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
    public static View show(Context context, OnEntryAddedListener listener) {
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
        return dialog;
    }

    public interface OnEntryAddedListener {
        void onEntryAdded(DiaryEntry entry);
    }
}