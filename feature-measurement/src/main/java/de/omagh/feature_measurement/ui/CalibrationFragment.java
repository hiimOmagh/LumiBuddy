package de.omagh.feature_measurement.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import de.omagh.feature_measurement.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalibrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalibrationFragment extends Fragment {

    // Argument keys for fragment initialization

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public CalibrationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CalibrationFragment.
     */
    public static CalibrationFragment newInstance(String param1, String param2) {
        CalibrationFragment fragment = new CalibrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calibration, container, false);
        View startBtn = v.findViewById(R.id.startCalibrationBtn);
        startBtn.setOnClickListener(view ->
                Navigation.findNavController(view)
                        .navigate(R.id.calibrationWizardFragment));
        return v;
    }
}