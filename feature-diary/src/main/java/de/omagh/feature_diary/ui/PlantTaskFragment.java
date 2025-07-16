package de.omagh.feature_diary.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.omagh.feature_diary.R;

public class PlantTaskFragment extends Fragment {
    public static final String ARG_PLANT_ID = "plant_id";
    private String plantId;
    private TaskViewModel viewModel;

    public PlantTaskFragment() {
    }

    public static PlantTaskFragment newInstance(String plantId) {
        PlantTaskFragment fragment = new PlantTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PLANT_ID, plantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            plantId = getArguments().getString(ARG_PLANT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plant_tasks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.taskRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        TaskAdapter adapter = new TaskAdapter(task -> viewModel.completeTask(task));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this,
                new TaskViewModel.Factory(requireActivity().getApplication()))
                .get(TaskViewModel.class);
        viewModel.getPendingTasks(plantId)
                .observe(getViewLifecycleOwner(), adapter::submitList);
    }
}
