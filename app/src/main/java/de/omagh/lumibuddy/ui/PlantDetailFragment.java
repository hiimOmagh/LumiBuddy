package de.omagh.lumibuddy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;

/**
 * Fragment to display details of a single plant and allow editing.
 * Receives plant data via arguments (name, type, imageUri), sets up ViewModel for future expandability.
 */
public class PlantDetailFragment extends Fragment {
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri"; // Add this!

    private PlantDetailViewModel viewModel;
    private ImageView plantImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the detail layout
        View view = inflater.inflate(R.layout.fragment_plant_detail, container, false);

        // Bind views
        TextView nameView = view.findViewById(R.id.detailPlantName);
        TextView typeView = view.findViewById(R.id.detailPlantType);
        plantImageView = view.findViewById(R.id.detailPlantImage);
        FloatingActionButton editFab = view.findViewById(R.id.editPlantFab);

        // Set up ViewModel
        viewModel = new ViewModelProvider(this).get(PlantDetailViewModel.class);

        // Get passed-in arguments
        Bundle args = getArguments();
        String name = (args != null) ? args.getString(ARG_NAME, "Plant") : "Plant";
        String type = (args != null) ? args.getString(ARG_TYPE, "Unknown") : "Unknown";
        String imageUri = (args != null) ? args.getString(ARG_IMAGE_URI, "") : "";

        // Set plant in ViewModel (use actual ID in future, or pass via nav args)
        viewModel.setPlant(new Plant("0", name, type, imageUri)); // "0" = dummy ID for now

        // Observe plant details for UI updates
        viewModel.getPlant().observe(getViewLifecycleOwner(), plant -> {
            nameView.setText(plant.getName());
            typeView.setText("Type: " + plant.getType());
            if (plant.getImageUri() != null && !plant.getImageUri().isEmpty()) {
                plantImageView.setImageURI(Uri.parse(plant.getImageUri()));
            } else {
                plantImageView.setImageResource(R.drawable.ic_eco); // fallback icon
            }
        });

        // Handle edit button click
        editFab.setOnClickListener(v -> showEditPlantDialog());

        return view;
    }

    /**
     * Shows a dialog to edit plant details.
     * Updates ViewModel (and DB in future) if user saves.
     */
    private void showEditPlantDialog() {
        Plant current = viewModel.getPlant().getValue();
        if (current == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_plant, null);
        EditText nameInput = dialogView.findViewById(R.id.editPlantName);
        EditText typeInput = dialogView.findViewById(R.id.editPlantType);
        // You could add image editing here as well if desired.

        nameInput.setText(current.getName());
        typeInput.setText(current.getType());

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Plant")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String type = typeInput.getText().toString().trim();
                    String imageUri = current.getImageUri(); // For now, keep same image
                    if (!name.isEmpty() && !type.isEmpty()) {
                        Plant updated = new Plant(current.getId(), name, type, imageUri);
                        viewModel.setPlant(updated);
                        // TODO: Call updatePlant in your ViewModel or parent (PlantListViewModel) for DB update.
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
