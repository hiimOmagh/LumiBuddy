package de.omagh.feature_measurement.ui;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import javax.inject.Inject;

import de.omagh.MeasurementUtils;
import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.feature_measurement.CalibrationManager;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.feature_growlight.GrowLightProfileManager;
import de.omagh.lumibuddy.feature_growlight.LampProduct;
import de.omagh.lumibuddy.feature_user.CalibrationProfilesManager;
import de.omagh.lumibuddy.feature_user.SettingsManager;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * ViewModel for light measurement, lamp type selection, and calculation of PPFD/DLI.
 */
public class MeasureViewModel extends AndroidViewModel {
    private final MutableLiveData<Float> luxLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> ppfdLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Integer> hoursLiveData = new MutableLiveData<>(24);
    private final MutableLiveData<Float> dliLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<LampType> lampTypeLiveData = new MutableLiveData<>(LampType.SUNLIGHT);
    private final MutableLiveData<Float> calibrationFactorLiveData = new MutableLiveData<>(CalibrationManager.DEFAULT_FACTOR);
    private final CalibrationProfilesManager profileManager;
    private final GrowLightProfileManager growLightManager;
    private final SettingsManager settingsManager;
    private final MutableLiveData<String> lampIdLiveData;
    @Inject
    GetCurrentLuxUseCase getCurrentLuxUseCase;
    private Disposable luxDisposable;
    private String currentSource = "ALS";

    public MeasureViewModel(@NonNull Application application) {
        super(application);
        ((LumiBuddyApplication) application).getAppComponent().inject(this);
        CalibrationManager calibrationManager = new CalibrationManager(application.getApplicationContext());
        profileManager = new CalibrationProfilesManager(application.getApplicationContext());
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

    // --- Data updaters ---
    public void setLux(float lux) {
        setLux(lux, "ALS");
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

    public void setHours(int hours) {
        if (hours < 1) hours = 1;
        if (hours > 24) hours = 24;
        hoursLiveData.postValue(hours);
        settingsManager.setLightDuration(hours);
        updateDLI(getPPFDValue(), hours);
    }

    /**
     * LiveData for DLI
     */
    public LiveData<Float> getDLI() {
        return dliLiveData;
    }

    /**
     * LiveData for active lamp profile ID.
     */
    public LiveData<String> getLampProfileId() {
        return lampIdLiveData;
    }

    public void setLampProfileId(String id) {
        growLightManager.setActiveLampProfile(id);
        settingsManager.setSelectedCalibrationProfileId(id);
        lampIdLiveData.setValue(id);
        updatePPFD(getLuxValue());
    }

    /**
     * LiveData for the current calibration factor in PPFD/lux.
     */
    public LiveData<Float> getCalibrationFactor() {
        return calibrationFactorLiveData;
    }

    // --- ALS management ---
    public void startMeasuring() {
        luxDisposable = getCurrentLuxUseCase.execute()
                .subscribe(lux -> setLux(lux, "ALS"), Timber::e);
    }

    public void stopMeasuring() {
        if (luxDisposable != null && !luxDisposable.isDisposed()) {
            luxDisposable.dispose();
        }
        getCurrentLuxUseCase.stop();
    }

    public void setLux(float lux, String source) {
        currentSource = source;
        luxLiveData.postValue(lux);
        updatePPFD(lux);
    }

    // --- Internal calculations ---
    private void updatePPFD(float lux) {
        String id = lampIdLiveData.getValue();
        updatePPFD(lux, id != null ? id : growLightManager.getActiveLampProfile().id);
    }

    private void updatePPFD(float lux, String lampId) {
        float lampFactor = CalibrationManager.DEFAULT_FACTOR;
        LampProduct def = growLightManager.getById(lampId);
        if (def != null) {
            lampFactor = def.calibrationFactor;
        }
        float deviceFactor = profileManager.getCalibrationFactorForSource(currentSource);
        float factor = lampFactor * deviceFactor;
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
}
