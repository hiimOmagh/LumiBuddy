package de.omagh.feature_diary.ui;

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

import de.omagh.feature_diary.R;
import de.omagh.core_data.model.DiaryEntry;

/**
 * Adapter for displaying a list of DiaryEntry items in a RecyclerView (growth timeline).
 */
public class DiaryEntryAdapter extends ListAdapter<DiaryEntry, RecyclerView.ViewHolder> {

    private final OnEntryInteractionListener listener;

    public DiaryEntryAdapter(OnEntryInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

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

    public interface OnEntryInteractionListener {
        void onEdit(DiaryEntry entry);

        void onDelete(DiaryEntry entry);
    }

    @Override
    public RecyclerView.@NonNull ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_diary_ar_scan, parent, false);
            return new ArScanViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diary_entry, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.@NonNull ViewHolder holder, int position) {
        DiaryEntry entry = getItem(position);
        if (holder instanceof ArScanViewHolder) {
            ((ArScanViewHolder) holder).bind(entry);
        } else if (holder instanceof DiaryViewHolder) {
            ((DiaryViewHolder) holder).bind(entry);
        }
    }

    @Override
    public int getItemViewType(int position) {
        DiaryEntry entry = getItem(position);
        if ("ar_scan".equals(entry.getEventType())) {
            return 1;
        }
        return 0;
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

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onEdit(entry));
                itemView.setOnLongClickListener(v -> {
                    listener.onDelete(entry);
                    return true;
                });
            }
        }
    }

    static class ArScanViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        ArScanViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.arImage);
        }

        void bind(DiaryEntry entry) {
            if (entry.getImageUri() != null && !entry.getImageUri().isEmpty()) {
                try {
                    imageView.setImageURI(Uri.parse(entry.getImageUri()));
                } catch (Exception e) {
                    imageView.setImageResource(R.drawable.ic_eco);
                }
            } else {
                imageView.setImageResource(R.drawable.ic_eco);
            }

            if (listener != null) {
                itemView.setOnClickListener(v -> listener.onEdit(entry));
                itemView.setOnLongClickListener(v -> {
                    listener.onDelete(entry);
                    return true;
                });
            }
        }
    }
}
