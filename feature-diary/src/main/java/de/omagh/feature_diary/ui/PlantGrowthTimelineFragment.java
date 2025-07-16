package de.omagh.feature_diary.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.omagh.feature_diary.ui.dialog.DiaryEntryDialog;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.omagh.core_data.model.DiaryEntry;
import de.omagh.feature_diary.ui.DiaryEntryAdapter;
import de.omagh.feature_diary.ui.DiaryViewModel;
import de.omagh.feature_diary.R;


public class PlantGrowthTimelineFragment extends Fragment {

    public static final String ARG_PLANT_ID = "plant_id";
    private String plantId;

    private DiaryViewModel diaryViewModel;
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
        DiaryEntryAdapter adapter = new DiaryEntryAdapter(new DiaryEntryAdapter.OnEntryInteractionListener() {
            @Override
            public void onEdit(DiaryEntry entry) {
                DiaryEntryDialog.edit(requireContext(), entry, edited -> diaryViewModel.updateEntry(edited));
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
        DiaryEntryDialog.show(requireContext(), entry -> {
            DiaryEntry fixed = new DiaryEntry(
                    entry.getId(),
                    plantId,
                    entry.getTimestamp(),
                    entry.getNote(),
                    entry.getImageUri(),
                    entry.getEventType()
            );
            diaryViewModel.addEntry(fixed);
            Snackbar.make(requireView(), R.string.add_diary_entry, Snackbar.LENGTH_SHORT).show();
        });
    }
}