package de.omagh.feature_diary.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_domain.model.Plant;
import de.omagh.feature_plantdb.ui.PlantListViewModel;
import de.omagh.feature_diary.R;
import de.omagh.feature_diary.ui.DiaryEntryAdapter;
import de.omagh.core_data.model.DiaryViewModel;

/**
 * Diary tab showing all diary entries across plants.
 */
public class PlantDiaryFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private PlantListViewModel plantViewModel;
    private DiaryEntryAdapter adapter;
    private Uri selectedImageUri;
    private ImageView dialogImagePreview;

    private final androidx.activity.result.ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = de.omagh.lumibuddy.util.ImageUtils.copyUriToInternalStorage(
                            requireContext(), uri);
                    if (dialogImagePreview != null) {
                        dialogImagePreview.setImageURI(selectedImageUri);
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.diaryRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        adapter = new DiaryEntryAdapter();
        recyclerView.setAdapter(adapter);

        diaryViewModel = new ViewModelProvider(this,
                new DiaryViewModel.Factory(requireActivity().getApplication()))
                .get(DiaryViewModel.class);
        plantViewModel = new ViewModelProvider(requireActivity()).get(PlantListViewModel.class);

        diaryViewModel.getAllEntries().observe(getViewLifecycleOwner(), entries -> {
            adapter.submitList(entries);
            View empty = view.findViewById(R.id.emptyState);
            if (entries == null || entries.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.addDiaryEntryFab);
        fab.setOnClickListener(v -> showAddEntryDialog());
    }

    private void showAddEntryDialog() {
        View dialog = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_diary_entry_global, null);
        Spinner plantSpinner = dialog.findViewById(R.id.plantSpinner);
        Spinner typeSpinner = dialog.findViewById(R.id.eventTypeSpinner);
        EditText noteInput = dialog.findViewById(R.id.editDiaryNote);
        dialogImagePreview = dialog.findViewById(R.id.diaryImagePreview);
        dialog.findViewById(R.id.pickImageBtn).setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        List<Plant> plants = plantViewModel.getPlants().getValue();
        List<Plant> safeList = plants != null ? plants : new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Plant p : safeList) {
            adapter.add(p.getName());
        }
        plantSpinner.setAdapter(adapter);

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_diary_entry)
                .setView(dialog)
                .setPositiveButton(R.string.save, (d, which) -> {
                    if (safeList.isEmpty()) {
                        Snackbar.make(requireView(), R.string.plant_list_desc, Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    Plant selected = safeList.get(plantSpinner.getSelectedItemPosition());
                    DiaryEntry entry = new DiaryEntry(
                            UUID.randomUUID().toString(),
                            selected.getId(),
                            System.currentTimeMillis(),
                            noteInput.getText().toString().trim(),
                            selectedImageUri != null ? selectedImageUri.toString() : "",
                            typeSpinner.getSelectedItem().toString()
                    );
                    diaryViewModel.addEntry(entry);
                    Snackbar.make(requireView(), R.string.add_diary_entry, Snackbar.LENGTH_SHORT).show();
                    selectedImageUri = null;
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}