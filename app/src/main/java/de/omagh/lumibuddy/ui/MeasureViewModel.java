package de.omagh.lumibuddy.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import de.omagh.lumibuddy.feature_measurement.ALSManager;
import de.omagh.lumibuddy.feature_measurement.MeasurementEngine;

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

    private final MeasurementEngine measurementEngine;

    public MeasureViewModel(@NonNull Application application) {
        super(application);
        measurementEngine = new MeasurementEngine(application.getApplicationContext());
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
     * LiveData for lamp type
     */
    public LiveData<LampType> getLampType() {
        return lampTypeLiveData;
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
        updateDLI(getPPFDValue(), hours);
    }

    public void setLampType(LampType lampType) {
        lampTypeLiveData.setValue(lampType);
        updatePPFD(getLuxValue());
    }

    // --- Internal calculations ---
    private void updatePPFD(float lux) {
        LampType type = lampTypeLiveData.getValue();
        updatePPFD(lux, type != null ? type : LampType.SUNLIGHT);
    }

    private void updatePPFD(float lux, LampType lampType) {
        float ppfd = lux * lampType.luxToPPFD;
        ppfdLiveData.setValue(ppfd);
        updateDLI(ppfd, getHoursValue());
    }

    private void updateDLI(float ppfd, int hours) {
        float dli = (ppfd * hours * 3600f) / 1_000_000f;
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
