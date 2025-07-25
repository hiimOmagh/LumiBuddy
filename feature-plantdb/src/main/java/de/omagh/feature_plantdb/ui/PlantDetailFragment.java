package de.omagh.feature_plantdb.ui;

import android.Manifest;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_plantdb.di.DaggerPlantDbComponent;
import de.omagh.feature_plantdb.di.PlantDbComponent;
import de.omagh.core_infra.util.PermissionUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Context;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.omagh.core_data.model.PlantCareProfileEntity;
import de.omagh.core_data.model.PlantSpecies;
import de.omagh.core_domain.model.Plant;
import de.omagh.feature_plantdb.ui.PlantDetailViewModel;
import de.omagh.feature_plantdb.R;

/**
 * Fragment to display details of a single plant and allow editing.
 * Receives plant data via arguments (name, type, imageUri), and binds a ViewModel.
 */
public class PlantDetailFragment extends Fragment {

    public static final String ARG_ID = "plant_id";
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";
    @Inject
    PlantDbViewModelFactory viewModelFactory;
    private PlantDetailViewModel viewModel;
    private PlantListViewModel listViewModel;
    private ImageView plantImageView;
    private TextView careInfoView;
    private EditText dialogNameInput;
    private EditText dialogTypeInput;
    private ImageView dialogImagePreview;
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && dialogImagePreview != null) {
                    Uri internal = de.omagh.core_infra.util.ImageUtils.copyUriToInternalStorage(requireContext(), uri);
                    dialogImagePreview.setImageURI(internal);
                    try {
                        android.graphics.Bitmap bmp = android.graphics.ImageDecoder.decodeBitmap(
                                android.graphics.ImageDecoder.createSource(requireActivity().getContentResolver(), internal));
                        identifyWithApi(bmp);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            });
    private final ActivityResultLauncher<Void> captureImageLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bmp -> {
                if (bmp != null && dialogImagePreview != null) {
                    dialogImagePreview.setImageBitmap(bmp);
                    identifyWithApi(bmp);
                }
            });
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    captureImageLauncher.launch(null);
                } else {
                    PermissionUtils.showPermissionDenied(this, getString(R.string.camera_permission_required));
                }
            });

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        PlantDbComponent component = DaggerPlantDbComponent.factory().create(core);
        viewModelFactory = component.viewModelFactory();
    }

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
        viewModel = new ViewModelProvider(this, viewModelFactory).get(PlantDetailViewModel.class);
        listViewModel = new ViewModelProvider(requireActivity(), viewModelFactory)
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
            typeView.setText(getString(R.string.type_format, plant.getType()));
            if (plant.getImageUri() != null && !plant.getImageUri().isEmpty()) {
                try {
                    plantImageView.setImageURI(Uri.parse(plant.getImageUri()));
                } catch (Exception e) {
                    plantImageView.setImageResource(R.drawable.ic_eco);
                }
            } else {
                plantImageView.setImageResource(R.drawable.ic_eco);
            }
            careInfoView.setText(R.string.care_info_placeholder);
        });

        // Edit FAB
        editFab.setOnClickListener(v -> showEditPlantDialog());

        // View timeline button opens diary timeline for this plant
        timelineButton.setOnClickListener(v -> {
            Plant current = viewModel.getPlant().getValue();
            if (current == null) return;
            Bundle bundle = new Bundle();
            bundle.putString(de.omagh.feature_diary.ui.PlantGrowthTimelineFragment.ARG_PLANT_ID,
                    current.getId());
            androidx.navigation.Navigation.findNavController(v)
                    .navigate(R.id.action_PlantDetailFragment_to_GrowthTimelineFragment, bundle);
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
                String msg = getString(R.string.plant_care_summary,
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
        dialogNameInput = dialogView.findViewById(R.id.editPlantName);
        dialogTypeInput = dialogView.findViewById(R.id.editPlantType);
        dialogImagePreview = dialogView.findViewById(R.id.plantImagePreview);
        Button searchPlantBtn = dialogView.findViewById(R.id.searchPlantBtn);
        View pickImageBtn = dialogView.findViewById(R.id.pickImageBtn);
        View captureImageBtn = dialogView.findViewById(R.id.captureImageBtn);

        dialogNameInput.setText(current.getName());
        dialogTypeInput.setText(current.getType());

        if (dialogImagePreview != null && current.getImageUri() != null && !current.getImageUri().isEmpty()) {
            try {
                dialogImagePreview.setImageURI(Uri.parse(current.getImageUri()));
            } catch (Exception e) {
                dialogImagePreview.setImageResource(R.drawable.ic_eco);
            }
        }

        pickImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        captureImageBtn.setOnClickListener(v -> {
            if (PermissionUtils.hasPermission(requireContext(), Manifest.permission.CAMERA)) {
                captureImageLauncher.launch(null);
            } else {
                PermissionUtils.requestPermissionWithRationale(
                        this,
                        Manifest.permission.CAMERA,
                        getString(R.string.camera_permission_rationale),
                        cameraPermissionLauncher);
            }
        });

        searchPlantBtn.setOnClickListener(v -> {
            // Trigger remote species search using the ViewModel
            String query = dialogNameInput.getText().toString().trim();
            viewModel.searchSpecies(query).observe(getViewLifecycleOwner(), results -> {
                if (results == null || results.isEmpty()) return;
                String[] names = new String[results.size()];
                for (int i = 0; i < results.size(); i++) names[i] = results.get(i).getCommonName();
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Select Plant")
                        .setItems(names, (d, which) -> {
                            PlantSpecies info = results.get(which);
                            dialogNameInput.setText(info.getCommonName());
                            dialogTypeInput.setText(info.getScientificName());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        });

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit Plant")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newName = dialogNameInput.getText().toString().trim();
                    String newType = dialogTypeInput.getText().toString().trim();

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

    private void identifyWithApi(android.graphics.Bitmap bmp) {
        Toast.makeText(getContext(), "Identifying...", Toast.LENGTH_SHORT).show();
        viewModel.identifyPlantWithApi(bmp);
        viewModel.getIdentificationResult().removeObservers(getViewLifecycleOwner());
        viewModel.getIdentificationResult().observe(getViewLifecycleOwner(), suggestion -> {
            if (suggestion != null) {
                if (dialogNameInput != null) dialogNameInput.setText(suggestion.getCommonName());
                if (dialogTypeInput != null)
                    dialogTypeInput.setText(suggestion.getScientificName());
                Toast.makeText(getContext(), "Identified with Plant.id", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Identification failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
