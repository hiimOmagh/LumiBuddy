package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.feature_growlight.GrowLightProfileManager;
import de.omagh.lumibuddy.feature_growlight.LampProduct;
import de.omagh.lumibuddy.feature_user.SettingsManager;

/**
 * Simple settings screen for user preferences.
 */
public class SettingsFragment extends Fragment {
    private SettingsManager settingsManager;
    private GrowLightProfileManager lampManager;

    private Spinner unitsSpinner;
    private EditText hoursInput;
    private Spinner calibrationSpinner;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsManager = new SettingsManager(requireContext());
        lampManager = new GrowLightProfileManager(requireContext());

        unitsSpinner = view.findViewById(R.id.unitsSpinner);
        hoursInput = view.findViewById(R.id.lightDurationInput);
        calibrationSpinner = view.findViewById(R.id.calibrationSpinner);

        // Unit spinner values from resources
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.measurement_units,
                android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitAdapter);

        // Populate lamp profiles
        ArrayAdapter<String> lampAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item);
        lampAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (LampProduct p : lampManager.getAllProfiles()) {
            lampAdapter.add(p.name);
        }
        calibrationSpinner.setAdapter(lampAdapter);

        // Set current values
        String unit = settingsManager.getPreferredUnit();
        int unitPos = unitAdapter.getPosition(unit);
        if (unitPos >= 0) unitsSpinner.setSelection(unitPos);
        hoursInput.setText(String.valueOf(settingsManager.getLightDuration()));

        String activeId = settingsManager.getSelectedCalibrationProfileId();
        if (activeId.isEmpty()) {
            activeId = lampManager.getActiveLampProfile().id;
        }
        java.util.List<LampProduct> lamps = lampManager.getAllProfiles();
        for (int i = 0; i < lamps.size(); i++) {
            if (lamps.get(i).id.equalsIgnoreCase(activeId)) {
                calibrationSpinner.setSelection(i);
                break;
            }
        }

        view.findViewById(R.id.saveSettingsBtn).setOnClickListener(v -> saveSettings());

        return view;
    }

    private void saveSettings() {
        String unit = (String) unitsSpinner.getSelectedItem();
        settingsManager.setPreferredUnit(unit);

        try {
            int hours = Integer.parseInt(hoursInput.getText().toString());
            if (hours < 1) hours = 1;
            if (hours > 24) hours = 24;
            settingsManager.setLightDuration(hours);
        } catch (NumberFormatException ignored) {
        }

        int lampIndex = calibrationSpinner.getSelectedItemPosition();
        java.util.List<LampProduct> lamps = lampManager.getAllProfiles();
        if (lampIndex >= 0 && lampIndex < lamps.size()) {
            LampProduct lamp = lamps.get(lampIndex);
            lampManager.setActiveLampProfile(lamp.id);
            settingsManager.setSelectedCalibrationProfileId(lamp.id);
        }

        Toast.makeText(requireContext(), R.string.save, Toast.LENGTH_SHORT).show();
    }
}