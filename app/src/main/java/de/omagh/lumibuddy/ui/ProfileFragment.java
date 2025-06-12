package de.omagh.lumibuddy.ui;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import de.omagh.lumibuddy.R;

/**
 * Fragment displaying and editing the local user profile.
 * Uses {@link ProfileViewModel} for data access.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private ImageView avatarView;
    private TextView nameView;
    private ActivityResultLauncher<String> imagePickerLauncher;

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
                        viewModel.setAvatarUri(uri.toString());
                    }
                });

        viewModel.getUsername().observe(getViewLifecycleOwner(), nameView::setText);
        viewModel.getAvatarUri().observe(getViewLifecycleOwner(), this::loadAvatar);
    }

    private void loadAvatar(String uri) {
        if (uri == null) {
            avatarView.setImageResource(R.drawable.ic_person);
        } else {
            avatarView.setImageURI(Uri.parse(uri));
        }
    }

    private void pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1001);
                return;
            }
        }
        imagePickerLauncher.launch("image/*");
    }

    private void showEditNameDialog() {
        final EditText input = new EditText(requireContext());
        input.setText(viewModel.getUsername().getValue());
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.edit_profile)
                .setView(input)
                .setPositiveButton(android.R.string.ok, (d, w) -> {
                    viewModel.setUsername(input.getText().toString().trim());
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}