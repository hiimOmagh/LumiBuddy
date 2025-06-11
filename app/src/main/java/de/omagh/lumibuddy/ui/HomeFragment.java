package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.navigation.Navigation;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import de.omagh.lumibuddy.R;

/**
 * HomeFragment displays the home screen and summary metrics.
 */
public class HomeFragment extends Fragment {
    private HomeViewModel mViewModel;

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

        view.findViewById(R.id.startMeasureButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.measureFragment));

        view.findViewById(R.id.addPlantButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.addPlantFragment));

        view.findViewById(R.id.viewTimelineButton).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.plantDiaryFragment));

        return view;
    }
}
