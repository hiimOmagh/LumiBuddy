package de.omagh.lumibuddy.ui;

import android.Manifest;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import java.util.UUID;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_ar.DummyARGrowthTracker;
import de.omagh.lumibuddy.feature_ml.BasicHealthStatusClassifier;
import de.omagh.lumibuddy.feature_ml.BasicPlantClassifier;
import de.omagh.lumibuddy.feature_ml.HealthStatusClassifier;
import de.omagh.lumibuddy.feature_ml.PlantClassifier;
import de.omagh.lumibuddy.util.PermissionUtils;
import de.omagh.lumibuddy.util.ImageUtils;

/**
 * Fragment to add or edit a plant.
 * If an ID is passed in arguments, the plant will be edited.
 */

public class AddPlantFragment extends Fragment {

    public static final String ARG_PLANT_ID = "plant_id";
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";
    private EditText nameInput, typeInput;
    private ImageView imagePreview;
    private PlantListViewModel plantListViewModel;
    private AddPlantViewModel addPlantViewModel;
    private Uri selectedImageUri = null;
    private String existingPlantId = null;
    // ML plant recognition
    private PlantClassifier plantClassifier;
    private HealthStatusClassifier healthClassifier;
    private DummyARGrowthTracker growthTracker;
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = de.omagh.lumibuddy.util.ImageUtils.copyUriToInternalStorage(
                            requireContext(), uri);
                    imagePreview.setImageURI(selectedImageUri);
                    try {
                        android.graphics.Bitmap bmp = android.graphics.ImageDecoder.decodeBitmap(
                                android.graphics.ImageDecoder.createSource(
                                        requireActivity().getContentResolver(), selectedImageUri));
                        recognizePlant(bmp);
                        identifyWithApi(bmp);
                        if (healthClassifier != null) {
                            healthClassifier.classify(bmp);
                            Log.d("AddPlant", "Health result=" + healthClassifier.getLastResult());
                        }
                        if (growthTracker != null) {
                            growthTracker.trackGrowth(bmp);
                        }
                    } catch (java.io.IOException e) {
                        Log.e("AddPlant", "Image decode error", e);
                    }
                }
            });
    private final ActivityResultLauncher<Void> captureImageLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bmp -> {
                if (bmp != null) {
                    imagePreview.setImageBitmap(bmp);
                    recognizePlant(bmp);
                    identifyWithApi(bmp);
                    if (healthClassifier != null) {
                        healthClassifier.classify(bmp);
                        Log.d("AddPlant", "Health result=" + healthClassifier.getLastResult());
                    }
                    if (growthTracker != null) {
                        growthTracker.trackGrowth(bmp);
                    }
                    selectedImageUri = null;
                }
            });
    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    captureImageLauncher.launch(null);
                } else {
                    PermissionUtils.showPermissionDenied(this,
                            getString(R.string.camera_permission_required));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_plant, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init UI
        nameInput = view.findViewById(R.id.editPlantName);
        typeInput = view.findViewById(R.id.editPlantType);
        imagePreview = view.findViewById(R.id.plantImagePreview);
        MaterialButton saveBtn = view.findViewById(R.id.savePlantBtn);
        MaterialButton pickImageBtn = view.findViewById(R.id.pickImageBtn);
        MaterialButton captureImageBtn = view.findViewById(R.id.captureImageBtn);
        MaterialButton searchPlantBtn = view.findViewById(R.id.searchPlantBtn);
        SwitchCompat mlToggle = view.findViewById(R.id.mlToggle);
        SwitchCompat arGrowthToggle = view.findViewById(R.id.arGrowthToggle);

        // Init ViewModel
        plantListViewModel = new ViewModelProvider(requireActivity()).get(PlantListViewModel.class);
        addPlantViewModel = new ViewModelProvider(this).get(AddPlantViewModel.class);

        de.omagh.lumibuddy.feature_user.SettingsManager sm =
                new de.omagh.lumibuddy.feature_user.SettingsManager(requireContext());
        mlToggle.setChecked(sm.isMlFeaturesEnabled());
        arGrowthToggle.setChecked(sm.isArOverlayEnabled());
        if (mlToggle.isChecked()) {
            plantClassifier = new BasicPlantClassifier();
            healthClassifier = new BasicHealthStatusClassifier();
        }
        if (arGrowthToggle.isChecked()) {
            growthTracker = new DummyARGrowthTracker();
            growthTracker.init();
        }

        mlToggle.setOnCheckedChangeListener((b, checked) -> {
            sm.setMlFeaturesEnabled(checked);
            if (checked) {
                plantClassifier = new BasicPlantClassifier();
                healthClassifier = new BasicHealthStatusClassifier();
                Toast.makeText(getContext(), "ML features enabled (stub)", Toast.LENGTH_SHORT).show();
            } else {
                plantClassifier = null;
                healthClassifier = null;
            }
        });

        arGrowthToggle.setOnCheckedChangeListener((b, checked) -> {
            sm.setArOverlayEnabled(checked);
            if (checked) {
                growthTracker = new DummyARGrowthTracker();
                growthTracker.init();
                Toast.makeText(getContext(), "AR growth tracking enabled (stub)", Toast.LENGTH_SHORT).show();
            } else if (growthTracker != null) {
                growthTracker.cleanup();
                growthTracker = null;
            }
        });
        // Check for edit mode
        Bundle args = getArguments();
        if (args != null) {
            existingPlantId = args.getString(ARG_PLANT_ID);
            nameInput.setText(args.getString(ARG_NAME, ""));
            typeInput.setText(args.getString(ARG_TYPE, ""));
            String uriStr = args.getString(ARG_IMAGE_URI, "");
            if (!TextUtils.isEmpty(uriStr)) {
                selectedImageUri = Uri.parse(uriStr);
                imagePreview.setImageURI(selectedImageUri);
            }
        }

        // Pick image button
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
        searchPlantBtn.setOnClickListener(v -> performPlantSearch());

        // Save button
        saveBtn.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String type = typeInput.getText().toString().trim();
            String imageUriStr = selectedImageUri != null ? selectedImageUri.toString() : "";

            if (name.isEmpty() || type.isEmpty()) {
                Toast.makeText(getContext(), "Please enter name and type", Toast.LENGTH_SHORT).show();
                return;
            }

            if (existingPlantId == null) {
                // Add new
                String newId = UUID.randomUUID().toString();
                Plant newPlant = new Plant(newId, name, type, imageUriStr);
                plantListViewModel.addPlant(newPlant);
            } else {
                // Update existing
                Plant updated = new Plant(existingPlantId, name, type, imageUriStr);
                plantListViewModel.updatePlant(updated);
            }

            // Go back after save
            androidx.navigation.Navigation.findNavController(requireView()).popBackStack();

        });
    }

    private void recognizePlant(android.graphics.Bitmap bitmap) {
        if (plantClassifier == null) return;
        plantClassifier.classify(bitmap);
        String result = plantClassifier.getLastResult();
        android.widget.Toast.makeText(getContext(), "Recognized: " + result,
                android.widget.Toast.LENGTH_SHORT).show();
        if (!android.text.TextUtils.isEmpty(result)) {
            nameInput.setText(result);
            typeInput.setText(result);
            performPlantSearch();
        }
    }

    private void performPlantSearch() {
        String query = nameInput.getText().toString().trim();
        plantListViewModel.searchPlantInfo(query).observe(getViewLifecycleOwner(), results -> {
            if (results == null || results.isEmpty()) {
                Toast.makeText(getContext(), "No match", Toast.LENGTH_SHORT).show();
                return;
            }
            String[] names = new String[results.size()];
            for (int i = 0; i < results.size(); i++) {
                names[i] = results.get(i).getCommonName();
            }
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Select Plant")
                    .setItems(names, (d, which) -> {
                        de.omagh.lumibuddy.data.model.PlantSpecies s = results.get(which);
                        nameInput.setText(s.getCommonName());
                        typeInput.setText(s.getScientificName());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void identifyWithApi(Bitmap bmp) {
        Toast.makeText(getContext(), "Identifying...", Toast.LENGTH_SHORT).show();
        addPlantViewModel.identifyPlant(bmp).observe(getViewLifecycleOwner(), result -> {
            if (result != null) {
                nameInput.setText(result.getCommonName());
                typeInput.setText(result.getScientificName());
                Toast.makeText(getContext(), getString(R.string.powered_by_plantid), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Identification failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
