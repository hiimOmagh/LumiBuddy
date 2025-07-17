package de.omagh.feature_measurement.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import de.omagh.feature_measurement.R;
import de.omagh.core_domain.model.CalibrationProfile;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;

/**
 * Simple settings screen for user preferences.
 */
public class SettingsFragment extends Fragment {
    private SettingsManager settingsManager;
    private CalibrationProfilesManager calibrationManager;
    private LightCorrectionRepository lightCorrectionStore;
    private List<CalibrationProfile> profileList = new ArrayList<>();
    private Spinner unitsSpinner;
    private EditText hoursInput;
    private Spinner calibrationSpinner;
    private android.widget.TextView calibrationInfoText;
    private Spinner lightTypeFactorSpinner;
    private android.widget.TextView lightFactorInfoText;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        settingsManager = new SettingsManager(requireContext());
        calibrationManager = new CalibrationProfilesManager(requireContext());
        CoreComponent core = ((CoreComponentProvider) requireContext().getApplicationContext()).getCoreComponent();
        lightCorrectionStore = core.lightCorrectionRepository();

        unitsSpinner = view.findViewById(R.id.unitsSpinner);
        hoursInput = view.findViewById(R.id.lightDurationInput);
        calibrationSpinner = view.findViewById(R.id.calibrationSpinner);
        View addCalibrationBtn = view.findViewById(R.id.addCalibrationBtn);
        calibrationInfoText = view.findViewById(R.id.calibrationInfoText);
        lightTypeFactorSpinner = view.findViewById(R.id.lightTypeFactorSpinner);
        lightFactorInfoText = view.findViewById(R.id.lightFactorInfoText);
        View editLightFactorBtn = view.findViewById(R.id.editLightFactorBtn);
        SwitchCompat careReminderSwitch = view.findViewById(R.id.careReminderSwitch);
        View syncNowBtn = view.findViewById(R.id.syncNowBtn);
        View privacyPolicyBtn = view.findViewById(R.id.privacyPolicyBtn);

        // Unit spinner values from resources
        ArrayAdapter<CharSequence> unitAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.measurement_units,
                android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitAdapter);

        // Light type correction spinner
        ArrayAdapter<CharSequence> lightAdapter = ArrayAdapter.createFromResource(
                requireContext(), R.array.lamp_types, android.R.layout.simple_spinner_item);
        lightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lightTypeFactorSpinner.setAdapter(lightAdapter);

        // Populate calibration profiles
        ArrayAdapter<String> calibAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item);
        calibAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileList = new ArrayList<>(calibrationManager.getProfiles());
        for (CalibrationProfile p : profileList) {
            calibAdapter.add(p.name);
        }
        calibAdapter.add(getString(R.string.add));
        calibrationSpinner.setAdapter(calibAdapter);
        updateCalibrationInfo();
        updateLightFactorInfo();

        calibrationSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                if (position == profileList.size()) {
                    showAddProfileDialog();
                } else {
                    if (position >= 0 && position < profileList.size()) {
                        CalibrationProfile p = profileList.get(position);
                        calibrationManager.setActiveProfile(p.id);
                        settingsManager.setSelectedCalibrationProfileId(p.id);
                        updateCalibrationInfo();
                    }
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        calibrationSpinner.setOnLongClickListener(v -> {
            int pos = calibrationSpinner.getSelectedItemPosition();
            if (pos >= 0 && pos < profileList.size()) {
                CalibrationProfile p = profileList.get(pos);
                showManageDialog(p);
                return true;
            }
            return false;
        });

        addCalibrationBtn.setOnClickListener(v -> showAddProfileDialog());

        lightTypeFactorSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view1, int position, long id) {
                updateLightFactorInfo();
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
        editLightFactorBtn.setOnClickListener(v -> showEditLightFactorDialog());

        careReminderSwitch.setChecked(settingsManager.isCareRemindersEnabled());
        careReminderSwitch.setOnCheckedChangeListener(
                (buttonView, isChecked) -> settingsManager.setCareRemindersEnabled(isChecked));

        // Set current values
        String unit = settingsManager.getPreferredUnit();
        int unitPos = unitAdapter.getPosition(unit);
        if (unitPos >= 0) unitsSpinner.setSelection(unitPos);
        hoursInput.setText(String.valueOf(settingsManager.getLightDuration()));

        String activeId = settingsManager.getSelectedCalibrationProfileId();
        if (activeId != null && !activeId.isEmpty()) {
            for (int i = 0; i < profileList.size(); i++) {
                if (profileList.get(i).id.equals(activeId)) {
                    calibrationSpinner.setSelection(i);
                    break;
                }
            }
        }

        view.findViewById(R.id.saveSettingsBtn).setOnClickListener(v -> saveSettings());

        syncNowBtn.setOnClickListener(v -> {
            // Schedule a one-time backup worker which uploads local data
            androidx.work.WorkManager.getInstance(requireContext())
                    .enqueue(new androidx.work.OneTimeWorkRequest.Builder(
                            de.omagh.core_infra.sync.BackupWorker.class)
                            .build());
            Toast.makeText(requireContext(), getString(R.string.coming_soon),
                    Toast.LENGTH_SHORT).show();
        });

        privacyPolicyBtn.setOnClickListener(v ->
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle(R.string.privacy_policy)
                        .setMessage(R.string.privacy_summary)
                        .setPositiveButton(android.R.string.ok, null)
                        .show());

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

        int index = calibrationSpinner.getSelectedItemPosition();
        if (index >= 0 && index < profileList.size()) {
            CalibrationProfile p = profileList.get(index);
            calibrationManager.setActiveProfile(p.id);
            settingsManager.setSelectedCalibrationProfileId(p.id);
        }

        updateCalibrationInfo();

        Toast.makeText(requireContext(), R.string.save, Toast.LENGTH_SHORT).show();
    }

    private void updateCalibrationInfo() {
        CalibrationProfile p = null;
        String id = settingsManager.getSelectedCalibrationProfileId();
        for (CalibrationProfile cp : profileList) {
            if (cp.id.equals(id)) {
                p = cp;
                break;
            }
        }
        if (p != null) {
            calibrationInfoText.setText(String.format("%s (%s)", getString(R.string.calibration_factor, p.calibrationFactor), p.source));
        } else {
            calibrationInfoText.setText("");
        }
    }

    private void updateLightFactorInfo() {
        String type = (String) lightTypeFactorSpinner.getSelectedItem();
        if (type != null) {
            float f = lightCorrectionStore.getFactor(type);
            lightFactorInfoText.setText(getString(R.string.calibration_factor, f));
        }
    }

    private void showEditLightFactorDialog() {
        String type = (String) lightTypeFactorSpinner.getSelectedItem();
        final EditText input = new EditText(getContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText(String.valueOf(lightCorrectionStore.getFactor(type)));
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(type)
                .setView(input)
                .setPositiveButton(R.string.save, (d, w) -> {
                    try {
                        float val = Float.parseFloat(input.getText().toString());
                        lightCorrectionStore.setFactor(type, val);
                        updateLightFactorInfo();
                    } catch (NumberFormatException ex) {
                        Toast.makeText(getContext(), R.string.invalid_number, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void showAddProfileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_calibration_profile, null);
        EditText name = dialogView.findViewById(R.id.editProfileName);
        Spinner source = dialogView.findViewById(R.id.editProfileSource);
        EditText factor = dialogView.findViewById(R.id.editProfileFactor);
        EditText note = dialogView.findViewById(R.id.editProfileNote);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(),
                R.array.calibration_sources, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        source.setAdapter(adapter);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.add);
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.add, (d, w) -> {
            try {
                String id = java.util.UUID.randomUUID().toString();
                float f = Float.parseFloat(factor.getText().toString());
                CalibrationProfile p = new CalibrationProfile(id, name.getText().toString(),
                        source.getSelectedItem().toString(), f, note.getText().toString());
                calibrationManager.addProfile(p);
                settingsManager.setSelectedCalibrationProfileId(id);
                profileList = new ArrayList<>(calibrationManager.getProfiles());
                ((ArrayAdapter<String>) calibrationSpinner.getAdapter()).insert(p.name, profileList.size() - 1);
                calibrationSpinner.setSelection(profileList.size() - 1);
                updateCalibrationInfo();
            } catch (NumberFormatException ex) {
                Toast.makeText(getContext(), R.string.invalid_number, Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void showManageDialog(CalibrationProfile profile) {
        String[] options = new String[]{getString(R.string.rename), getString(R.string.delete)};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(profile.name)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameDialog(profile);
                    } else if (which == 1) {
                        calibrationManager.removeProfile(profile.id);
                        refreshSpinner();
                    }
                }).show();
    }

    private void showRenameDialog(CalibrationProfile profile) {
        final EditText input = new EditText(getContext());
        input.setText(profile.name);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.rename)
                .setView(input)
                .setPositiveButton(R.string.save, (d, w) -> {
                    profileList.remove(profile);
                    CalibrationProfile updated = new CalibrationProfile(profile.id, input.getText().toString(),
                            profile.source, profile.calibrationFactor, profile.note);
                    calibrationManager.updateProfile(updated);
                    refreshSpinner();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void refreshSpinner() {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) calibrationSpinner.getAdapter();
        adapter.clear();
        profileList = new ArrayList<>(calibrationManager.getProfiles());
        for (CalibrationProfile p : profileList) {
            adapter.add(p.name);
        }
        adapter.add(getString(R.string.add));
        adapter.notifyDataSetChanged();
        String activeId = settingsManager.getSelectedCalibrationProfileId();
        int pos = 0;
        for (int i = 0; i < profileList.size(); i++) {
            if (profileList.get(i).id.equals(activeId)) {
                pos = i;
                break;
            }
        }
        calibrationSpinner.setSelection(pos);
        updateCalibrationInfo();
    }
}
