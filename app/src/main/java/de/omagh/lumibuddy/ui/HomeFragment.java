package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.Manifest;
import android.content.pm.PackageManager;

import java.util.Locale;

import androidx.navigation.Navigation;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import de.omagh.lumibuddy.data.model.Plant;
import de.omagh.lumibuddy.feature_diary.DiaryViewModel;
import de.omagh.lumibuddy.feature_recommendation.NotificationManager;
import de.omagh.lumibuddy.feature_recommendation.RecommendationEngine;
import de.omagh.lumibuddy.feature_recommendation.WateringScheduler;
import de.omagh.lumibuddy.data.db.AppDatabase;
import de.omagh.lumibuddy.ui.PlantListViewModel;
import de.omagh.lumibuddy.feature_user.SettingsManager;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.omagh.lumibuddy.R;

/**
 * HomeFragment displays the home screen and summary metrics.
 */
public class HomeFragment extends Fragment {
    /**
     * Temporarily stores the plant list while waiting for notification
     * permission to be granted. The list is cleared once the daily check
     * has been executed.
     */
    private java.util.List<Plant> pendingPlants;
    private final java.util.concurrent.ExecutorService lightCheckExecutor =
            java.util.concurrent.Executors.newSingleThreadExecutor();

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        TextView welcomeText = view.findViewById(R.id.homeWelcomeText);
        TextView luxValue = view.findViewById(R.id.luxValue);
        TextView ppfdValue = view.findViewById(R.id.ppfdValue);
        TextView dliValue = view.findViewById(R.id.dliValueHome);
        LinearLayout taskList = view.findViewById(R.id.taskList);

        final SettingsManager settingsManager = new SettingsManager(requireContext());

        RecommendationEngine engine = new RecommendationEngine();
        NotificationManager nm = new NotificationManager(requireContext());
        WateringScheduler scheduler = new WateringScheduler(engine, nm,
                AppDatabase.getInstance(requireContext()).diaryDao());
        DiaryViewModel diaryVm = new ViewModelProvider(this,
                new DiaryViewModel.Factory(requireActivity().getApplication()))
                .get(DiaryViewModel.class);

        ActivityResultLauncher<String> notifPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted && pendingPlants != null) {
                        // Double-check permission before triggering the scheduler
                        if (ContextCompat.checkSelfPermission(requireContext(),
                                Manifest.permission.POST_NOTIFICATIONS)
                                == PackageManager.PERMISSION_GRANTED) {
                            scheduler.runDailyCheck(pendingPlants);
                        }
                        pendingPlants = null;
                    }
                });

        HomeViewModel viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getWelcomeText().observe(getViewLifecycleOwner(), welcomeText::setText);
        viewModel.getLux().observe(getViewLifecycleOwner(),
                v -> luxValue.setText(String.format(Locale.getDefault(), "%.0f", v)));
        viewModel.getPpfd().observe(getViewLifecycleOwner(),
                v -> ppfdValue.setText(String.format(Locale.getDefault(), "%.0f", v)));
        viewModel.getDli().observe(getViewLifecycleOwner(),
                v -> dliValue.setText(String.format(Locale.getDefault(), "%.1f", v)));

        // Fix: Don't re-declare or re-assign inflater! Use the one provided by the method parameter.
        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            taskList.removeAllViews();
            for (String t : tasks) {
                TextView tv = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, taskList, false);
                tv.setText(t);
                taskList.addView(tv);
            }
        });

        PlantListViewModel plantVm = new ViewModelProvider(requireActivity(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()))
                .get(PlantListViewModel.class);

        plantVm.getPlants().observe(getViewLifecycleOwner(), plants -> {
            if (!settingsManager.isCareRemindersEnabled()) return;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                        == PackageManager.PERMISSION_GRANTED) {
                    scheduler.runDailyCheck(plants);
                    lightCheckExecutor.execute(() ->
                            engine.checkLightRecommendations(plants,
                                    diaryVm::getDiaryEntriesForPlantSync,
                                    diaryVm::addEntry));
                } else {
                    pendingPlants = plants;
                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                scheduler.runDailyCheck(plants);
                lightCheckExecutor.execute(() ->
                        engine.checkLightRecommendations(plants,
                                diaryVm::getDiaryEntriesForPlantSync,
                                diaryVm::addEntry));
            }
        });

        view.findViewById(R.id.startMeasureButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.measureFragment));

        view.findViewById(R.id.addPlantButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addPlantFragment));

        view.findViewById(R.id.viewTimelineButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.plantListFragment));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lightCheckExecutor.shutdown();
    }
}
