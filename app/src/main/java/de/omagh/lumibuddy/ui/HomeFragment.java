package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.navigation.Navigation;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import de.omagh.lumibuddy.data.model.Plant;
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
    private HomeViewModel mViewModel;
    private SettingsManager settingsManager;
    private ActivityResultLauncher<String> notifPermissionLauncher;
    private java.util.List<Plant> pendingPlants;

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

        settingsManager = new SettingsManager(requireContext());

        RecommendationEngine engine = new RecommendationEngine();
        NotificationManager nm = new NotificationManager(requireContext());
        WateringScheduler scheduler = new WateringScheduler(engine, nm,
                AppDatabase.getInstance(requireContext()).diaryDao());

        notifPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted && pendingPlants != null) {
                        scheduler.runDailyCheck(pendingPlants);
                        pendingPlants = null;
                    }
                });

        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mViewModel.getWelcomeText().observe(getViewLifecycleOwner(), welcomeText::setText);
        mViewModel.getLux().observe(getViewLifecycleOwner(), v -> luxValue.setText(String.format("%.0f", v)));
        mViewModel.getPpfd().observe(getViewLifecycleOwner(), v -> ppfdValue.setText(String.format("%.0f", v)));
        mViewModel.getDli().observe(getViewLifecycleOwner(), v -> dliValue.setText(String.format("%.1f", v)));

        // Fix: Don't re-declare or re-assign inflater! Use the one provided by the method parameter.
        mViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
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
                } else {
                    pendingPlants = plants;
                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }
            } else {
                scheduler.runDailyCheck(plants);
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
}
