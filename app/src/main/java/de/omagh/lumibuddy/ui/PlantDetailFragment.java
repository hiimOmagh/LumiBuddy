package de.omagh.lumibuddy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;

/**
 * Fragment to display details of a single plant and allow editing.
 * Receives plant data via arguments (name, type, imageUri), and binds a ViewModel.
 */
public class PlantDetailFragment extends Fragment {

    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";

    private PlantDetailViewModel viewModel;
    private ImageView plantImageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plant_detail, container, false);

        // UI elements
        TextView nameView = view.findViewById(R.id.detailPlantName);
        TextView typeView = view.findViewById(R.id.detailPlantType);
        plantImageView = view.findViewById(R.id.detailPlantImage);
        FloatingActionButton editFab = view.findViewById(R.id.editPlantFab);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(PlantDetailViewModel.class);

        // Retrieve arguments
        Bundle args = getArguments();
        String name = (args != null) ? args.getString(ARG_NAME, "Unknown") : "Unknown";
        String type = (args != null) ? args.getString(ARG_TYPE, "Unknown") : "Unknown";
        String imageUri = (args != null) ? args.getString(ARG_IMAGE_URI, "") : "";

        // For now, use dummy ID; in future use real ID and fetch from DB
        Plant passedPlant = new Plant("0", name, type, imageUri);
        viewModel.setPlant(passedPlant);

        // Observe LiveData for display
        viewModel.getPlant().observe(getViewLifecycleOwner(), plant -> {
            nameView.setText(plant.getName());
            typeView.setText("Type: " + plant.getType());

            if (plant.getImageUri() != null && !plant.getImageUri().isEmpty()) {
                plantImageView.setImageURI(Uri.parse(plant.getImageUri()));
            } else {
                plantImageView.setImageResource(R.drawable.ic_eco);
            }
        });

        // Edit FAB
        editFab.setOnClickListener(v -> showEditPlantDialog());

        return view;
    }

    /**
     * Shows a dialog to edit current plant and updates the ViewModel.
     * You can later extend this to persist changes via the database.
     */
    private void showEditPlantDialog() {
        Plant current = viewModel.getPlant().getValue();
        if (current == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_plant, null);
        EditText nameInput = dialogView.findViewById(R.id.editPlantName);
        EditText typeInput = dialogView.findViewById(R.id.editPlantType);
        ImageView imagePreview = dialogView.findViewById(R.id.plantImagePreview);

        nameInput.setText(current.getName());
        typeInput.setText(current.getType());

        if (imagePreview != null && current.getImageUri() != null && !current.getImageUri().isEmpty()) {
            imagePreview.setImageURI(Uri.parse(current.getImageUri()));
        }

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Plant")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = nameInput.getText().toString().trim();
                    String newType = typeInput.getText().toString().trim();

                    if (newName.isEmpty() || newType.isEmpty()) {
                        Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Plant updated = new Plant(current.getId(), newName, newType, current.getImageUri());
                    viewModel.setPlant(updated);

                    // TODO: Save updated plant to DB using a shared ViewModel or repository
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
