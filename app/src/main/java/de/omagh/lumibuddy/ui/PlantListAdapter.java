package de.omagh.lumibuddy.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.R;

/**
 * Adapter for displaying a list of plants in a RecyclerView.
 * Supports click for details and long-press for delete.
 */
public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantViewHolder> {

    private List<Plant> plants;
    private OnPlantClickListener clickListener;
    private OnPlantDeleteListener deleteListener;

    // Click for detail
    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
    }

    public void setOnPlantClickListener(OnPlantClickListener listener) {
        this.clickListener = listener;
    }

    // Long-press for delete
    public interface OnPlantDeleteListener {
        void onPlantDelete(Plant plant);
    }

    public void setOnPlantDeleteListener(OnPlantDeleteListener listener) {
        this.deleteListener = listener;
    }

    public PlantListAdapter(List<Plant> plants) {
        this.plants = plants;
    }

    public void updatePlants(List<Plant> newPlants) {
        this.plants = newPlants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.plantName.setText(plant.getName());
        holder.plantType.setText(plant.getType());

        // Set accessibility content descriptions
        holder.plantName.setContentDescription("Plant name: " + plant.getName());
        holder.plantType.setContentDescription("Plant type: " + plant.getType());

        // Click for detail
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onPlantClick(plant);
        });

        // Long-press for delete
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onPlantDelete(plant);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return plants != null ? plants.size() : 0;
    }

    static class PlantViewHolder extends RecyclerView.ViewHolder {
        TextView plantName, plantType;

        PlantViewHolder(View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plantName);
            plantType = itemView.findViewById(R.id.plantType);
        }
    }

}
