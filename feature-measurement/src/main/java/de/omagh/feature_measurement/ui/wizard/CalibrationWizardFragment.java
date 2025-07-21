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
import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.widget.Toast;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.feature_measurement.R;

/**
 * Simple three step wizard that allows the user to record a correction
 * factor for a specific light type. The result is stored via
 * {@link de.omagh.core_infra.user.CalibrationProfilesManager}.
 */
public class CalibrationWizardFragment extends Fragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private int step = 0;
    private Spinner typeSpinner;
    private TextView instructions;
    private Button nextBtn;
    private LightCorrectionRepository correctionStore;
    private MeasurementRepository measurementRepository;
    private AppExecutors executors;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        CoreComponent core = ((CoreComponentProvider) context.getApplicationContext()).getCoreComponent();
        correctionStore = core.lightCorrectionRepository();
        measurementRepository = core.measurementRepository();
        executors = core.appExecutors();
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
            ProgressDialog dlg = ProgressDialog.show(requireContext(), null,
                    getString(R.string.calibrating), true, false);
            String type = (String) typeSpinner.getSelectedItem();
            disposables.add(
                    measurementRepository.observeLux()
                            .take(20)
                            .reduce(new float[]{0f, 0f}, (acc, val) -> {
                                acc[0] += val;
                                acc[1] += 1f;
                                return acc;
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(data -> {
                                dlg.dismiss();
                                float avg = data[0] / Math.max(1f, data[1]);
                                float factor = 1f / Math.max(1f, avg);
                                correctionStore.setFactor(type, factor);
                                step = 2;
                                updateUi();
                            }, e -> {
                                dlg.dismiss();
                                Toast.makeText(getContext(), R.string.sensor_error,
                                        Toast.LENGTH_LONG).show();
                            }));
            return;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
