package de.omagh.feature_measurement.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.feature_measurement.R;
import de.omagh.core_infra.measurement.LampProduct;

/**
 * Adapter for displaying grow light profiles.
 */
public class LampProfileAdapter extends RecyclerView.Adapter<LampProfileAdapter.LampViewHolder> {
    private List<LampProduct> lamps;
    private OnLampClickListener clickListener;
    private OnLampDeleteListener deleteListener;
    private OnLampEditListener editListener;

    public LampProfileAdapter(List<LampProduct> lamps) {
        this.lamps = lamps;
    }

    public void setOnLampClickListener(OnLampClickListener l) {
        this.clickListener = l;
    }

    public void setOnLampDeleteListener(OnLampDeleteListener l) {
        this.deleteListener = l;
    }

    public void setOnLampEditListener(OnLampEditListener l) {
        this.editListener = l;
    }

    public void update(List<LampProduct> newLamps) {
        lamps = newLamps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LampViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lamp_profile, parent, false);
        return new LampViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LampViewHolder holder, int position) {
        LampProduct lamp = lamps.get(position);
        holder.name.setText(lamp.name);
        String details = lamp.brand + " • " + lamp.type + " • " + lamp.wattage + "W";
        holder.details.setText(details);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onLampClick(lamp);
        });

        holder.editButton.setOnClickListener(v -> {
            if (editListener != null) editListener.onLampEdit(lamp);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onLampDelete(lamp);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return lamps != null ? lamps.size() : 0;
    }

    public interface OnLampClickListener {
        void onLampClick(LampProduct lamp);
    }

    public interface OnLampDeleteListener {
        void onLampDelete(LampProduct lamp);
    }

    public interface OnLampEditListener {
        void onLampEdit(LampProduct lamp);
    }

    static class LampViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView details;
        final android.widget.ImageButton editButton;

        LampViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.lampName);
            details = itemView.findViewById(R.id.lampDetails);
            editButton = itemView.findViewById(R.id.editLampButton);
        }
    }
}