package de.omagh.feature_growschedule.ui;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;
import androidx.navigation.Navigation;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;

import de.omagh.feature_growschedule.R;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.util.PermissionUtils;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.feature_growschedule.di.DaggerGrowScheduleComponent;
import de.omagh.feature_growschedule.di.GrowScheduleComponent;

public class HomeFragment extends Fragment {

    @Inject
    HomeViewModelFactory viewModelFactory;
    private HomeViewModel viewModel;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onAttach(@androidx.annotation.NonNull Context context) {
        super.onAttach(context);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        GrowScheduleComponent component = DaggerGrowScheduleComponent.factory().create(core);
        viewModelFactory = component.viewModelFactory();
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
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

        viewModel = new ViewModelProvider(this, viewModelFactory)
                .get(HomeViewModel.class);

        ActivityResultLauncher<String> notifPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                this::onActivityResult);

        viewModel.getWelcomeText().observe(getViewLifecycleOwner(), welcomeText::setText);
        viewModel.getLux().observe(getViewLifecycleOwner(),
                v -> luxValue.setText(String.format(Locale.getDefault(), "%.0f", v)));
        viewModel.getPpfd().observe(getViewLifecycleOwner(),
                v -> ppfdValue.setText(String.format(Locale.getDefault(), "%.0f", v)));
        viewModel.getDli().observe(getViewLifecycleOwner(),
                v -> dliValue.setText(String.format(Locale.getDefault(), "%.1f", v)));

        // Fix: Don't re-declare or re-assign inflater! Use the one provided by the method parameter.
        viewModel.getUpcomingReminders().observe(getViewLifecycleOwner(), tasks -> {
            taskList.removeAllViews();
            for (String t : tasks) {
                TextView tv = (TextView) inflater.inflate(android.R.layout.simple_list_item_1, taskList, false);
                tv.setText(t);
                taskList.addView(tv);
            }
        });

        viewModel.getPlants().observe(getViewLifecycleOwner(), plants -> {
            if (!settingsManager.isCareRemindersEnabled()) return;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    !PermissionUtils.hasPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)) {
                PermissionUtils.requestPermissionWithRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS,
                        getString(R.string.notification_permission_rationale),
                        notifPermissionLauncher);
            } else {
                try {
                    new de.omagh.core_infra.recommendation.WateringWorkScheduler(requireContext()).scheduleDaily();
                    viewModel.refresh();
                } catch (SecurityException ignored) {
                    // Permission might still be missing
                }
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
    }

    private void onActivityResult(Boolean granted) {
        if (granted) {
            try {
                viewModel.refresh();
            } catch (SecurityException ignored) {
                // Permission may still be denied
            }
        } else {
            PermissionUtils.showPermissionDenied(this,
                    getString(R.string.notification_permission_denied));
        }
    }
}
