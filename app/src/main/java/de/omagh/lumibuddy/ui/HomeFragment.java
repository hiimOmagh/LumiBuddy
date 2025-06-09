package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import de.omagh.lumibuddy.R;

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
        mViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        mViewModel.getWelcomeText().observe(getViewLifecycleOwner(), welcomeText::setText);

        // You can add more observers, e.g., for stats/notifications

        return view;
    }
}
