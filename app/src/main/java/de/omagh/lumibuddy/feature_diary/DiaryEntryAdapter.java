package de.omagh.lumibuddy.feature_diary;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;

/**
 * Adapter for displaying a list of NODiaryEntry items in a RecyclerView (growth timeline).
 */
public class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.DiaryViewHolder> {

    private List<DiaryEntry> entries = new ArrayList<>();

    public void submitList(List<DiaryEntry> newEntries) {
        this.entries = newEntries != null ? newEntries : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView timestampView, noteView, typeView;
        ImageView imageView;

        DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            timestampView = itemView.findViewById(R.id.entryTimestamp);
            noteView = itemView.findViewById(R.id.entryNote);
            typeView = itemView.findViewById(R.id.entryType);
            imageView = itemView.findViewById(R.id.entryImage);
        }

        void bind(DiaryEntry entry) {
            // Format timestamp
            String formattedTime = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    .format(new Date(entry.getTimestamp()));
            timestampView.setText(formattedTime);

            // Type (label or icon)
            typeView.setText(entry.getEventType().toUpperCase());

            // Note
            noteView.setText(entry.getNote());

            // Image (optional)
            if (entry.getImageUri() != null && !entry.getImageUri().isEmpty()) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageURI(Uri.parse(entry.getImageUri()));
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
