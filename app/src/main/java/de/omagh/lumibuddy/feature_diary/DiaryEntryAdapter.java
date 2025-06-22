package de.omagh.lumibuddy.feature_diary;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.omagh.lumibuddy.R;

/**
 * Adapter for displaying a list of DiaryEntry items in a RecyclerView (growth timeline).
 */
public class DiaryEntryAdapter extends ListAdapter<DiaryEntry, DiaryEntryAdapter.DiaryViewHolder> {

    private static final DiffUtil.ItemCallback<DiaryEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DiaryEntry oldItem, @NonNull DiaryEntry newItem) {
            return oldItem.getTimestamp() == newItem.getTimestamp()
                    && oldItem.getNote().equals(newItem.getNote())
                    && oldItem.getImageUri().equals(newItem.getImageUri())
                    && oldItem.getEventType().equals(newItem.getEventType());
        }
    };

    public DiaryEntryAdapter() {
        super(DIFF_CALLBACK);
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
        DiaryEntry entry = getItem(position);
        holder.bind(entry);
    }

    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        final TextView timestampView;
        final TextView noteView;
        final TextView typeView;
        final ImageView imageView;

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
                try {
                    imageView.setImageURI(Uri.parse(entry.getImageUri()));
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.ic_eco);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }
    }
}
