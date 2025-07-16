package de.omagh.feature_measurement.ui.wizard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.user.LightCorrectionStore;
import de.omagh.feature_measurement.R;

/**
 * Simple three step wizard that allows the user to record a correction
 * factor for a specific light type. The result is stored via
 * {@link CalibrationProfilesManager}.
 */
public class CalibrationWizardFragment extends Fragment {
    private int step = 0;
    private Spinner typeSpinner;
    private TextView instructions;
    private Button nextBtn;
    private LightCorrectionStore correctionStore;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        correctionStore = core.lightCorrectionStore();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calibration_wizard, container, false);
        typeSpinner = v.findViewById(R.id.lightTypeSpinner);
        instructions = v.findViewById(R.id.instructions);
        nextBtn = v.findViewById(R.id.nextBtn);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.lamp_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(adapter);

        nextBtn.setOnClickListener(v1 -> onNext());
        updateUi();
        return v;
    }

    private void onNext() {
        if (step == 0) {
            step = 1;
        } else if (step == 1) {
            // Normally this would capture sensor data to compute the factor
            float result = 0.02f; // stub value
            String type = (String) typeSpinner.getSelectedItem();
            correctionStore.setFactor(type, result);
            step = 2;
        } else {
            ActivityCompat.finishAffinity(requireActivity());
        }
        updateUi();
    }

    private void updateUi() {
        if (step == 0) {
            instructions.setText(R.string.calib_step_choose_type);
            nextBtn.setText(R.string.next);
        } else if (step == 1) {
            instructions.setText(R.string.calib_step_measure);
            nextBtn.setText(R.string.save);
        } else {
            instructions.setText(R.string.calib_step_done);
            nextBtn.setText(R.string.finish);
        }
    }
}
