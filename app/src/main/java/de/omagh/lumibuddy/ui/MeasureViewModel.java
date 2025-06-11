package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import de.omagh.lumibuddy.feature_measurement.ALSManager;
import de.omagh.lumibuddy.feature_measurement.MeasurementEngine;
import de.omagh.lumibuddy.feature_measurement.CalibrationManager;
import de.omagh.lumibuddy.feature_measurement.MeasurementUtils;
import de.omagh.lumibuddy.feature_growlight.GrowLightProfileManager;
import de.omagh.lumibuddy.feature_growlight.LampProduct;
import de.omagh.lumibuddy.feature_user.SettingsManager;

/**
 * ViewModel for light measurement, lamp type selection, and calculation of PPFD/DLI.
 */
public class MeasureViewModel extends AndroidViewModel {
    // Enum for lamp types and their lux-to-PPFD factors
    public enum LampType {
        SUNLIGHT(0.0185f),
        WHITE_LED(0.019f),
        WARM_LED(0.021f),
        BLURPLE_LED(0.045f),
        HPS(0.014f);

        public final float luxToPPFD;


        LampType(float factor) {
            this.luxToPPFD = factor;
        }
    }

    private final MutableLiveData<Float> luxLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> ppfdLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Integer> hoursLiveData = new MutableLiveData<>(24);
    private final MutableLiveData<Float> dliLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<LampType> lampTypeLiveData = new MutableLiveData<>(LampType.SUNLIGHT);
    private final MutableLiveData<Float> calibrationFactorLiveData = new MutableLiveData<>(CalibrationManager.DEFAULT_FACTOR);
    private final MeasurementEngine measurementEngine;
    private final CalibrationManager calibrationManager;
    private final GrowLightProfileManager growLightManager;
    private final SettingsManager settingsManager;
    private final MutableLiveData<String> lampIdLiveData;


    public MeasureViewModel(@NonNull Application application) {
        super(application);
        measurementEngine = new MeasurementEngine(application.getApplicationContext());
        calibrationManager = new CalibrationManager(application.getApplicationContext());
        growLightManager = new GrowLightProfileManager(application.getApplicationContext());
        settingsManager = new SettingsManager(application.getApplicationContext());

        int hours = settingsManager.getLightDuration();
        hoursLiveData.setValue(hours);

        String lampId = settingsManager.getSelectedCalibrationProfileId();
        if (lampId == null || lampId.isEmpty()) {
            lampId = growLightManager.getActiveLampProfile().id;
        } else {
            growLightManager.setActiveLampProfile(lampId);
        }
        lampIdLiveData = new MutableLiveData<>(lampId);
    }

    /**
     * LiveData for lux
     */
    public LiveData<Float> getLux() {
        return luxLiveData;
    }

    /**
     * LiveData for PPFD
     */
    public LiveData<Float> getPPFD() {
        return ppfdLiveData;
    }

    /**
     * LiveData for daily light hours
     */
    public LiveData<Integer> getHours() {
        return hoursLiveData;
    }

    /**
     * LiveData for DLI
     */
    public LiveData<Float> getDLI() {
        return dliLiveData;
    }

    /**
     * LiveData for active lamp profile ID.
     * */
    public LiveData<String> getLampProfileId() {
        return lampIdLiveData;
    }

    /**
     * LiveData for the current calibration factor in PPFD/lux.
     */
    public LiveData<Float> getCalibrationFactor() {
        return calibrationFactorLiveData;
    }

    // --- ALS management ---
    public void startMeasuring() {
        measurementEngine.startALS(this::setLux);
    }

    public void stopMeasuring() {
        measurementEngine.stopALS();
    }

    // --- Data updaters ---
    public void setLux(float lux) {
        luxLiveData.postValue(lux);
        updatePPFD(lux);
    }

    public void setHours(int hours) {
        if (hours < 1) hours = 1;
        if (hours > 24) hours = 24;
        hoursLiveData.postValue(hours);
        settingsManager.setLightDuration(hours);
        updateDLI(getPPFDValue(), hours);
    }

    public void setLampProfileId(String id) {
        growLightManager.setActiveLampProfile(id);
        settingsManager.setSelectedCalibrationProfileId(id);
        lampIdLiveData.setValue(id);
        updatePPFD(getLuxValue());
    }

    // --- Internal calculations ---
    private void updatePPFD(float lux) {
        String id = lampIdLiveData.getValue();
        updatePPFD(lux, id != null ? id : growLightManager.getActiveLampProfile().id);
    }

    private void updatePPFD(float lux, String lampId) {
        float factor = calibrationManager.getCalibrationFactor(lampId);
        LampProduct def = growLightManager.getById(lampId);
        if (factor == CalibrationManager.DEFAULT_FACTOR && def != null) {
            factor = def.calibrationFactor;
        }
        calibrationFactorLiveData.setValue(factor);

        float ppfd = MeasurementUtils.luxToPPFD(lux, factor);
        ppfdLiveData.setValue(ppfd);
        updateDLI(ppfd, getHoursValue());
    }

    private void updateDLI(float ppfd, int hours) {
        float dli = MeasurementUtils.ppfdToDLI(ppfd, hours);
        dliLiveData.setValue(dli);
    }

    private float getLuxValue() {
        Float v = luxLiveData.getValue();
        return (v == null ? 0f : v);
    }

    private int getHoursValue() {
        Integer v = hoursLiveData.getValue();
        return (v == null ? 24 : v);
    }

    private float getPPFDValue() {
        Float v = ppfdLiveData.getValue();
        return (v == null ? 0f : v);
    }
}
