package de.omagh.lumibuddy.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.jspecify.annotations.NonNull;

import java.util.List;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;


/**
 * Adapter for displaying a list of plants in a RecyclerView.
 * Supports click for details and long-press for deletion.
 */
public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.PlantViewHolder> {

    private List<Plant> plants;
    private OnPlantClickListener clickListener;
    private OnPlantDeleteListener deleteListener;

    public PlantListAdapter(List<Plant> plants) {
        this.plants = plants;
    }

    public void setOnPlantClickListener(OnPlantClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnPlantDeleteListener(OnPlantDeleteListener listener) {
        this.deleteListener = listener;
    }

    /**
     * Updates the list of plants and refreshes the view.
     */
    public void updatePlants(List<Plant> newPlants) {
        this.plants = newPlants;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PlantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant, parent, false);
        return new PlantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantViewHolder holder, int position) {
        Plant plant = plants.get(position);
        holder.plantName.setText(plant.getName());
        holder.plantType.setText(plant.getType());

        holder.plantName.setContentDescription("Plant name: " + plant.getName());
        holder.plantType.setContentDescription("Plant type: " + plant.getType());

        // Optional: Load image if present
        String imageUri = plant.getImageUri();
        if (imageUri != null && !imageUri.isEmpty()) {
            holder.plantImage.setImageURI(Uri.parse(imageUri));
        } else {
            holder.plantImage.setImageResource(R.drawable.ic_eco); // fallback/default
        }

        // Tap → open details
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onPlantClick(plant);
        });

        // Long press → confirm deletion
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
        return (plants != null) ? plants.size() : 0;
    }

    /**
     * Interface for handling plant item clicks (navigate to details).
     */
    public interface OnPlantClickListener {
        void onPlantClick(Plant plant);
    }

    /**
     * Interface for handling long-press deletions.
     */
    public interface OnPlantDeleteListener {
        void onPlantDelete(Plant plant);
    }

    /**
     * ViewHolder for plant items.
     */
    static class PlantViewHolder extends RecyclerView.ViewHolder {
        final TextView plantName;
        final TextView plantType;
        final ImageView plantImage;

        PlantViewHolder(@NonNull View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plantName);
            plantType = itemView.findViewById(R.id.plantType);
            plantImage = itemView.findViewById(R.id.plantImage); // needs to exist in layout
        }
    }
}
