package de.omagh.lumibuddy.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
    private Button saveBtn, pickImageBtn;

    private PlantListViewModel plantListViewModel;
    private Uri selectedImageUri = null;
    private String existingPlantId = null;

    public static final String ARG_PLANT_ID = "plant_id";
    public static final String ARG_NAME = "plant_name";
    public static final String ARG_TYPE = "plant_type";
    public static final String ARG_IMAGE_URI = "plant_image_uri";

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
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

        // Init ViewModel
        plantListViewModel = new ViewModelProvider(requireActivity()).get(PlantListViewModel.class);

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
}
