package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.feature_diary.DiaryEntry;
import de.omagh.lumibuddy.feature_diary.DiaryViewModel;
import de.omagh.lumibuddy.feature_diary.DiaryEntryAdapter;


public class PlantGrowthTimelineFragment extends Fragment {

    public static final String ARG_PLANT_ID = "plant_id";
    private String plantId;

    private DiaryViewModel diaryViewModel;
    private DiaryEntryAdapter adapter;

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
        adapter = new DiaryEntryAdapter();
        recyclerView.setAdapter(adapter);

        // ViewModel
        AppDatabase db = AppDatabase.getInstance(requireContext());
        diaryViewModel = new ViewModelProvider(this,
                new DiaryViewModel.Factory(db.diaryDao()))
                .get(DiaryViewModel.class);
        diaryViewModel.getDiaryEntriesForPlant(plantId)
                .observe(getViewLifecycleOwner(), adapter::submitList);

        // FAB to add dummy log (for now)
        FloatingActionButton fab = view.findViewById(R.id.addGrowthEventFab);
        fab.setOnClickListener(v -> {
            DiaryEntry entry = new DiaryEntry(
                    UUID.randomUUID().toString(),
                    plantId,
                    System.currentTimeMillis(),
                    "Sample note entry",
                    "",
                    "note"
            );
            diaryViewModel.addEntry(entry);
            Toast.makeText(getContext(), "Diary entry added", Toast.LENGTH_SHORT).show();
        });
    }
}
