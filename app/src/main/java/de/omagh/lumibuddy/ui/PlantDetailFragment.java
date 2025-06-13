package de.omagh.lumibuddy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.data.model.PlantCareProfileEntity;
import de.omagh.lumibuddy.feature_plantdb.PlantCareProfile;
import de.omagh.lumibuddy.feature_plantdb.PlantInfo;
import de.omagh.lumibuddy.feature_plantdb.PlantStage;
import de.omagh.lumibuddy.ui.PlantListViewModel;
import de.omagh.lumibuddy.ui.PlantGrowthTimelineFragment;

/**
 * Fragment to display details of a single plant and allow editing.
 * Receives plant data via arguments (name, type, imageUri), and binds a ViewModel.
 */
public class PlantDetailFragment extends Fragment {

    public static final String ARG_ID = "plant_id";
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";
    private PlantDetailViewModel viewModel;
    private PlantListViewModel listViewModel;
    private ImageView plantImageView;
    private TextView careInfoView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_plant_detail, container, false);

        // UI elements
        TextView nameView = view.findViewById(R.id.detailPlantName);
        TextView typeView = view.findViewById(R.id.detailPlantType);
        careInfoView = view.findViewById(R.id.detailCareInfo);
        plantImageView = view.findViewById(R.id.detailPlantImage);
        FloatingActionButton editFab = view.findViewById(R.id.editPlantFab);
        View timelineButton = view.findViewById(R.id.viewTimelineButton);
        View fetchDetails = view.findViewById(R.id.fetchMoreInfoButton);

        // ViewModels
        viewModel = new ViewModelProvider(this).get(PlantDetailViewModel.class);
        listViewModel = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PlantListViewModel.class);

        // Retrieve arguments
        Bundle args = getArguments();
        String id = (args != null) ? args.getString(ARG_ID, "0") : "0";
        String name = (args != null) ? args.getString(ARG_NAME, "Unknown") : "Unknown";
        String type = (args != null) ? args.getString(ARG_TYPE, "Unknown") : "Unknown";
        String imageUri = (args != null) ? args.getString(ARG_IMAGE_URI, "") : "";

        Plant passedPlant = new Plant(id, name, type, imageUri);
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
            careInfoView.setText(R.string.care_info_placeholder);
        });

        // Edit FAB
        editFab.setOnClickListener(v -> showEditPlantDialog());

        // View timeline button
        timelineButton.setOnClickListener(v -> {
            Plant current = viewModel.getPlant().getValue();
            if (current != null) {
                Bundle b = new Bundle();
                b.putString(PlantGrowthTimelineFragment.ARG_PLANT_ID, current.getId());
                androidx.navigation.Navigation.findNavController(v)
                        .navigate(R.id.plantGrowthTimelineFragment, b);
            }
        });

        fetchDetails.setOnClickListener(v -> {
            Plant current = viewModel.getPlant().getValue();
            if (current == null) return;
            viewModel.getCareProfile(current.getType()).observe(getViewLifecycleOwner(), profiles -> {
                if (profiles == null || profiles.isEmpty()) {
                    Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
                    return;
                }
                PlantCareProfileEntity p = profiles.get(0);
                String msg = String.format(java.util.Locale.US,
                        "Water every %d d\nTemp %.0f-%.0f°C\nHumidity %.0f-%.0f%%\nSunlight %s",
                        p.getWateringIntervalDays(),
                        p.getMinTemperature(), p.getMaxTemperature(),
                        p.getMinHumidity(), p.getMaxHumidity(),
                        p.getSunlightRequirement());
                android.widget.TextView tv = new android.widget.TextView(requireContext());
                tv.setText(msg);
                com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                        new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
                dialog.setContentView(tv);
                dialog.show();
            });
        });

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
        Button searchPlantBtn = dialogView.findViewById(R.id.searchPlantBtn);

        nameInput.setText(current.getName());
        typeInput.setText(current.getType());

        if (imagePreview != null && current.getImageUri() != null && !current.getImageUri().isEmpty()) {
            imagePreview.setImageURI(Uri.parse(current.getImageUri()));
        }

        searchPlantBtn.setOnClickListener(v -> {
            // Trigger remote species search using the ViewModel
            String query = nameInput.getText().toString().trim();
            viewModel.searchSpecies(query).observe(this, results -> {
                if (results == null || results.isEmpty()) return;
                String[] names = new String[results.size()];
                for (int i = 0; i < results.size(); i++) names[i] = results.get(i).getCommonName();
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Select Plant")
                        .setItems(names, (d, which) -> {
                            de.omagh.lumibuddy.data.model.PlantSpecies info = results.get(which);
                            nameInput.setText(info.getCommonName());
                            typeInput.setText(info.getScientificName());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });

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
                    if (listViewModel != null) {
                        listViewModel.updatePlant(updated);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
