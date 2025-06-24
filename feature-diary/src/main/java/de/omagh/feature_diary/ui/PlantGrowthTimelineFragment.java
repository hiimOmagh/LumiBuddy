package de.omagh.feature_diary.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.feature_diary.ui.DiaryEntryAdapter;
import de.omagh.core_data.model.DiaryViewModel;
import de.omagh.feature_diary.R;


public class PlantGrowthTimelineFragment extends Fragment {

    public static final String ARG_PLANT_ID = "plant_id";
    private String plantId;

    private DiaryViewModel diaryViewModel;
    private Uri selectedImageUri = null;
    private ImageView dialogImagePreview;

    private final androidx.activity.result.ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = de.omagh.core_infra.util.ImageUtils.copyUriToInternalStorage(
                            requireContext(), uri);
                    if (dialogImagePreview != null) {
                        dialogImagePreview.setImageURI(selectedImageUri);
                    }
                }
            });


    public PlantGrowthTimelineFragment() {
    }

    public static PlantGrowthTimelineFragment newInstance(String plantId) {
        PlantGrowthTimelineFragment fragment = new PlantGrowthTimelineFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_ID, plantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getString(ARG_PLANT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_growth_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.growthTimelineRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        DiaryEntryAdapter adapter = new DiaryEntryAdapter();
        recyclerView.setAdapter(adapter);

        // ViewModel
        diaryViewModel = new ViewModelProvider(this,
                new DiaryViewModel.Factory(requireActivity().getApplication()))
                .get(DiaryViewModel.class);
        diaryViewModel.getDiaryEntriesForPlant(plantId)
                .observe(getViewLifecycleOwner(), adapter::submitList);

        // FAB to add new log entry
        FloatingActionButton fab = view.findViewById(R.id.addGrowthEventFab);
        fab.setOnClickListener(v -> showAddEntryDialog());
    }

    private void showAddEntryDialog() {
        View dialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_diary_entry, null);
        Spinner typeSpinner = dialog.findViewById(R.id.eventTypeSpinner);
        EditText noteInput = dialog.findViewById(R.id.editDiaryNote);
        dialogImagePreview = dialog.findViewById(R.id.diaryImagePreview);
        dialog.findViewById(R.id.pickImageBtn).setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, (d, which) -> {
                    String note = noteInput.getText().toString().trim();
                    String type = typeSpinner.getSelectedItem().toString();
                    String uriStr = selectedImageUri != null ? selectedImageUri.toString() : "";
                    DiaryEntry entry = new DiaryEntry(
                            UUID.randomUUID().toString(),
                            plantId,
                            System.currentTimeMillis(),
                            note,
                            uriStr,
                            type
                    );
                    diaryViewModel.addEntry(entry);
                    selectedImageUri = null;
                    Snackbar.make(requireView(), R.string.add_diary_entry, Snackbar.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}