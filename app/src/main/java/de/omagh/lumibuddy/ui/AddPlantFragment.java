package de.omagh.lumibuddy.ui;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import de.omagh.lumibuddy.util.PermissionUtils;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.model.Plant;

/**
 * Fragment to add or edit a plant.
 * If an ID is passed in arguments, the plant will be edited.
 */

public class AddPlantFragment extends Fragment {

    private EditText nameInput, typeInput;
    private ImageView imagePreview;
    private MaterialButton saveBtn, pickImageBtn, captureImageBtn, searchPlantBtn;

    private PlantListViewModel plantListViewModel;
    private Uri selectedImageUri = null;
    private String existingPlantId = null;

    // ML plant recognition
    private de.omagh.lumibuddy.feature_ml.PlantRecognitionModel plantRecognizer;

    public static final String ARG_PLANT_ID = "plant_id";
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
                    try {
                        android.graphics.Bitmap bmp;
                        bmp = android.graphics.ImageDecoder.decodeBitmap(
                                android.graphics.ImageDecoder.createSource(
                                        requireActivity().getContentResolver(), uri));
                        recognizePlant(bmp);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<Void> captureImageLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicturePreview(), bmp -> {
                if (bmp != null) {
                    imagePreview.setImageBitmap(bmp);
                    recognizePlant(bmp);
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
        saveBtn = view.findViewById(R.id.savePlantBtn);
        pickImageBtn = view.findViewById(R.id.pickImageBtn);
        captureImageBtn = view.findViewById(R.id.captureImageBtn);
        searchPlantBtn = view.findViewById(R.id.searchPlantBtn);

        // Init ViewModel
        plantListViewModel = new ViewModelProvider(requireActivity()).get(PlantListViewModel.class);

        de.omagh.lumibuddy.feature_user.SettingsManager sm =
                new de.omagh.lumibuddy.feature_user.SettingsManager(requireContext());
        if (sm.isMlFeaturesEnabled()) {
            plantRecognizer = new de.omagh.lumibuddy.feature_ml.PlantClassifier();
            plantRecognizer.loadModel();
        }
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
        if (plantRecognizer == null) return;
        plantRecognizer.analyzeImage(bitmap);
        String result = plantRecognizer.getResult();
        android.widget.Toast.makeText(getContext(), "Recognized: " + result,
                android.widget.Toast.LENGTH_SHORT).show();
        if (!android.text.TextUtils.isEmpty(result)) {
            nameInput.setText(result);
            typeInput.setText(result);
            de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager db =
                    new de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager();
            de.omagh.lumibuddy.feature_plantdb.PlantIdentifier ider =
                    new de.omagh.lumibuddy.feature_plantdb.PlantIdentifier(db);
            de.omagh.lumibuddy.feature_plantdb.PlantInfo info = ider.identifyByName(result);
            if (info != null) showCareProfileDialog(info);
        }
    }

    private void performPlantSearch() {
        de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager db =
                new de.omagh.lumibuddy.feature_plantdb.PlantDatabaseManager();
        de.omagh.lumibuddy.feature_plantdb.PlantIdentifier ider =
                new de.omagh.lumibuddy.feature_plantdb.PlantIdentifier(db);
        String query = nameInput.getText().toString().trim();
        de.omagh.lumibuddy.feature_plantdb.PlantInfo match = ider.identifyByName(query);
        if (match != null) {
            nameInput.setText(match.commonName);
            typeInput.setText(match.scientificName);
            showCareProfileDialog(match);
            return;
        }

        java.util.List<de.omagh.lumibuddy.feature_plantdb.PlantInfo> all = db.getAllPlants();
        String[] names = new String[all.size()];
        for (int i = 0; i < all.size(); i++) names[i] = all.get(i).commonName;
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Select Plant")
                .setItems(names, (d, which) -> {
                    de.omagh.lumibuddy.feature_plantdb.PlantInfo info = all.get(which);
                    nameInput.setText(info.commonName);
                    typeInput.setText(info.scientificName);
                    showCareProfileDialog(info);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showCareProfileDialog(de.omagh.lumibuddy.feature_plantdb.PlantInfo info) {
        de.omagh.lumibuddy.feature_plantdb.PlantCareProfile p =
                info.getProfileForStage(de.omagh.lumibuddy.feature_plantdb.PlantStage.VEGETATIVE);
        if (p == null) return;
        String msg = String.format(java.util.Locale.US,
                "Light %.0f-%.0f μmol/m²/s\nWater every %d d\nHumidity %.0f-%.0f%%",
                p.getMinPPFD(), p.getMaxPPFD(),
                p.getWateringIntervalDays(),
                p.getMinHumidity(), p.getMaxHumidity());
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(info.commonName)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }
}
