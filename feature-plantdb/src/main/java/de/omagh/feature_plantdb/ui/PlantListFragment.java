package de.omagh.feature_plantdb.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.UUID;

import de.omagh.core_data.model.PlantSpecies;
import de.omagh.core_domain.model.Plant;
import de.omagh.feature_plantdb.R;
import de.omagh.feature_plantdb.data.PlantInfo;

/**
 * Fragment displaying a list of plants with add, delete, and detail support.
 * Tapping a plant navigates to its detail; long-pressing deletes it.
 */
public class PlantListFragment extends Fragment {
    private PlantListViewModel viewModel;
    private Uri pickedImageUri = null;
    private ImageView plantImagePreview = null;

    // Image picker for Add Plant dialog
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && plantImagePreview != null) {
// Copy to internal storage so permission persists
                    pickedImageUri = de.omagh.lumibuddy.util.ImageUtils.copyUriToInternalStorage(
                            requireContext(), uri);
                    plantImagePreview.setImageURI(pickedImageUri);
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plant_list, container, false);

        // RecyclerView setup
        RecyclerView recyclerView = view.findViewById(R.id.plantRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PlantListAdapter adapter = new PlantListAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // On plant click → navigate to details
        adapter.setOnPlantClickListener(plant -> {
            Bundle args = new Bundle();
            args.putString(PlantDetailFragment.ARG_ID, plant.getId());
            args.putString(PlantDetailFragment.ARG_NAME, plant.getName());
            args.putString(PlantDetailFragment.ARG_TYPE, plant.getType());
            args.putString(PlantDetailFragment.ARG_IMAGE_URI, plant.getImageUri());

            androidx.navigation.Navigation.findNavController(recyclerView)
                    .navigate(R.id.plantDetailFragment, args);
        });

        // On long press → delete with confirmation
        adapter.setOnPlantDeleteListener(plant ->
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Delete Plant")
                        .setMessage("Delete " + plant.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> viewModel.deletePlant(plant))
                        .setNegativeButton("Cancel", null)
                        .show());

        // FAB to add new plant
        FloatingActionButton addFab = view.findViewById(R.id.addPlantFab);
        addFab.setOnClickListener(v -> showAddPlantDialog());

        // ViewModel init
        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PlantListViewModel.class);

        // Observe LiveData for plant list
        viewModel.getPlants().observe(getViewLifecycleOwner(), adapter::updatePlants);

        return view;
    }

    /**
     * Shows a dialog to input a new plant's data (with optional image).
     */
    private void showAddPlantDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_plant, null);
        EditText nameInput = dialogView.findViewById(R.id.editPlantName);
        EditText typeInput = dialogView.findViewById(R.id.editPlantType);
        plantImagePreview = dialogView.findViewById(R.id.plantImagePreview);
        Button pickImageBtn = dialogView.findViewById(R.id.pickImageBtn);
        Button searchPlantBtn = dialogView.findViewById(R.id.searchPlantBtn);

        pickedImageUri = null;
        if (plantImagePreview != null) {
            plantImagePreview.setImageResource(R.drawable.ic_eco); // Default icon
        }

        pickImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        searchPlantBtn.setOnClickListener(v -> {
            String query = nameInput.getText().toString().trim();
            viewModel.searchPlantInfo(query).observe(getViewLifecycleOwner(), results -> {
                if (results != null && !results.isEmpty()) {
                    PlantSpecies species = results.get(0);
                    nameInput.setText(species.getCommonName());
                    typeInput.setText(species.getScientificName());
                    Toast.makeText(getContext(), "Loaded profile for " + species.getCommonName(), Toast.LENGTH_SHORT).show();
                    return;
                }

                java.util.List<PlantInfo> all = viewModel.getAllPlantInfo();
                String[] names = new String[all.size()];
                for (int i = 0; i < all.size(); i++) {
                    names[i] = all.get(i).commonName;
                }
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Select Plant")
                        .setItems(names, (d, which) -> {
                            PlantInfo info = all.get(which);
                            nameInput.setText(info.commonName);
                            typeInput.setText(info.scientificName);
                            Toast.makeText(getContext(), "Loaded profile for " + info.commonName, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Add New Plant")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String name = nameInput.getText().toString().trim();
                    String type = typeInput.getText().toString().trim();
                    String imageUriStr = pickedImageUri != null ? pickedImageUri.toString() : "";

                    if (name.isEmpty() || type.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String id = UUID.randomUUID().toString();
                    Plant newPlant = new Plant(id, name, type, imageUriStr);
                    viewModel.addPlant(newPlant);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
