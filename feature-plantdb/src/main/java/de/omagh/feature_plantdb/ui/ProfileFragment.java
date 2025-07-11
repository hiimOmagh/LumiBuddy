package de.omagh.feature_plantdb.ui;

import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.omagh.feature_plantdb.R;
import de.omagh.core_infra.util.ImageUtils;
import de.omagh.core_infra.util.PermissionUtils;

/**
 * Fragment displaying and editing the local user profile.
 * Uses {@link ProfileViewModel} for data access.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private ImageView avatarView;
    private TextView nameView;
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> imagePermissionLauncher;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avatarView = view.findViewById(R.id.profileAvatar);
        nameView = view.findViewById(R.id.profileName);
        view.findViewById(R.id.editProfileBtn).setOnClickListener(v -> showEditNameDialog());
        avatarView.setOnClickListener(v -> pickImage());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Uri internalUri = ImageUtils.copyUriToInternalStorage(requireContext(), uri);
                        viewModel.setAvatarUri(internalUri.toString());
                    }
                });

        imagePermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        imagePickerLauncher.launch("image/*");
                    } else {
                        PermissionUtils.showPermissionDenied(this,
                                getString(R.string.storage_permission_denied));
                    }
                });

        viewModel.getUsername().observe(getViewLifecycleOwner(), nameView::setText);
        viewModel.getAvatarUri().observe(getViewLifecycleOwner(), this::loadAvatar);
    }

    private void loadAvatar(String uri) {
        if (uri == null) {
            avatarView.setImageResource(R.drawable.ic_person);
        } else {
            try {
                avatarView.setImageURI(Uri.parse(uri));
            } catch (Exception e) {
                avatarView.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !PermissionUtils.hasPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)) {
            PermissionUtils.requestPermissionWithRationale(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    getString(R.string.storage_permission_rationale),
                    imagePermissionLauncher);
        } else {
            imagePickerLauncher.launch("image/*");
        }
    } // <-- FIX: This closes pickImage()

    private void showEditNameDialog() {
        final EditText input = new EditText(requireContext());
        input.setText(viewModel.getUsername().getValue());
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.edit_profile)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (d, w) -> viewModel.setUsername(input.getText().toString().trim()))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
