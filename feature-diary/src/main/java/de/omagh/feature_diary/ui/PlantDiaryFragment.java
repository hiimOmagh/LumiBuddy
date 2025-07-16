package de.omagh.feature_diary.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.omagh.feature_diary.ui.dialog.DiaryEntryDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_domain.model.Plant;

import de.omagh.feature_diary.R;
import de.omagh.feature_diary.ui.DiaryEntryAdapter;
import de.omagh.feature_diary.ui.DiaryViewModel;

/**
 * Diary tab showing all diary entries across plants.
 */
public class PlantDiaryFragment extends Fragment {

    private DiaryViewModel diaryViewModel;
    private DiaryEntryAdapter adapter;
    private ImageView dialogImagePreview;

    private final androidx.activity.result.ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new androidx.activity.result.contract.ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Uri selectedImageUri = de.omagh.core_infra.util.ImageUtils.copyUriToInternalStorage(
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
        adapter = new DiaryEntryAdapter(new DiaryEntryAdapter.OnEntryInteractionListener() {
            @Override
            public void onEdit(DiaryEntry entry) {
                DiaryEntryDialog.edit(requireContext(), entry, edited -> {
                    diaryViewModel.updateEntry(edited);
                    Snackbar.make(requireView(), R.string.edit_diary_entry, Snackbar.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onDelete(DiaryEntry entry) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setMessage(R.string.delete_diary_entry)
                        .setPositiveButton(R.string.delete, (d, w) -> diaryViewModel.deleteEntry(entry))
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        diaryViewModel = new ViewModelProvider(this,
                new DiaryViewModel.Factory(requireActivity().getApplication()))
                .get(DiaryViewModel.class);

        diaryViewModel.getAllEntries().observe(getViewLifecycleOwner(), entries -> {
            adapter.submitList(entries);
            View empty = view.findViewById(R.id.emptyState);
            if (entries == null || entries.isEmpty()) {
                empty.setVisibility(View.VISIBLE);
            } else {
                empty.setVisibility(View.GONE);
            }
        });
        diaryViewModel.getSyncStatus().observe(getViewLifecycleOwner(), s -> {
            if (s == de.omagh.core_infra.sync.SyncStatus.SYNCING) {
                Snackbar.make(recyclerView, "Syncing", Snackbar.LENGTH_SHORT).show();
            } else if (s == de.omagh.core_infra.sync.SyncStatus.ERROR) {
                String msg = diaryViewModel.getSyncError().getValue();
                Snackbar.make(recyclerView, "Sync failed: " + msg, Snackbar.LENGTH_LONG).show();
            }
        });
        diaryViewModel.triggerSync();

        FloatingActionButton fab = view.findViewById(R.id.addDiaryEntryFab);
        fab.setOnClickListener(v -> showAddEntryDialog());
    }

    private void showAddEntryDialog() {
        DiaryEntryDialog.show(requireContext(), entry -> {
            diaryViewModel.addEntry(entry);
            Snackbar.make(requireView(), R.string.add_diary_entry, Snackbar.LENGTH_SHORT).show();
        });
    }
}