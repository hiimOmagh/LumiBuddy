package de.omagh.feature_measurement.ui;

import android.app.Application;
import android.Manifest;

import androidx.annotation.RequiresPermission;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jspecify.annotations.NonNull;

import javax.inject.Inject;

import de.omagh.core_domain.util.MeasurementUtils;
import de.omagh.core_domain.usecase.GetCurrentLuxUseCase;
import de.omagh.core_infra.measurement.CalibrationManager;
import de.omagh.core_infra.measurement.GrowLightProfileManager;
import de.omagh.core_infra.measurement.LampProduct;
import de.omagh.core_infra.environment.SunlightEstimator;
import de.omagh.core_infra.user.CalibrationProfilesManager;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.core_infra.calibration.CalibrationRepository;
import de.omagh.core_data.repository.LightCorrectionRepository;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_infra.util.PermissionUtils;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

/**
 * ViewModel for light measurement, lamp type selection, and calculation of PPFD/DLI.
 */
public class MeasureViewModel extends AndroidViewModel {
    private final MutableLiveData<Float> luxLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> ppfdLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Integer> hoursLiveData = new MutableLiveData<>(24);
    private final MutableLiveData<Float> dliLiveData = new MutableLiveData<>(0f);
    private final MutableLiveData<Float> calibrationFactorLiveData;
    @Inject
    CalibrationProfilesManager profileManager;
    @Inject
    GrowLightProfileManager growLightManager;
    @Inject
    SettingsManager settingsManager;
    @Inject
    GetCurrentLuxUseCase getCurrentLuxUseCase;
    @Inject
    CalibrationManager calibrationManager;
    @Inject
    SunlightEstimator sunlightEstimator;
    @Inject
    DiaryRepository diaryRepository;
    @Inject
    LightCorrectionRepository lightCorrectionStore;
    @Inject
    CalibrationRepository calibrationRepository;
    private MutableLiveData<String> lampIdLiveData;
    private Disposable luxDisposable;
    private String currentSource = "ALS";

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Inject
    public MeasureViewModel(@NonNull Application application,
                            CalibrationProfilesManager profileManager,
                            GrowLightProfileManager growLightManager,
                            SettingsManager settingsManager,
                            GetCurrentLuxUseCase getCurrentLuxUseCase,
                            CalibrationManager calibrationManager,
                            SunlightEstimator sunlightEstimator,
                            DiaryRepository diaryRepository,
                            LightCorrectionRepository lightCorrectionStore,
                            CalibrationRepository calibrationRepository) {
        super(application);
        this.profileManager = profileManager;
        this.growLightManager = growLightManager;
        this.settingsManager = settingsManager;
        this.getCurrentLuxUseCase = getCurrentLuxUseCase;
        this.calibrationManager = calibrationManager;
        this.sunlightEstimator = sunlightEstimator;
        this.diaryRepository = diaryRepository;
        this.lightCorrectionStore = lightCorrectionStore;
        this.calibrationRepository = calibrationRepository;
        this.calibrationFactorLiveData = new MutableLiveData<>(calibrationRepository.getDefaultCalibrationFactor());

        int hours = settingsManager.getLightDuration();
        hoursLiveData.setValue(hours);
        if (settingsManager.isAutoSunlightEstimationEnabled()) {
            refreshSunlightEstimate();
        }

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
        float lampFactor = calibrationRepository.getDefaultCalibrationFactor();
        LampProduct def = growLightManager.getById(lampId);
        if (def != null) {
            lampFactor = def.calibrationFactor;
            lampFactor *= lightCorrectionStore.getFactor(def.type);
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

    private float getDliValue() {
        Float v = dliLiveData.getValue();
        return (v == null ? 0f : v);
    }

    /**
     * Re-computes the default light duration using {@link SunlightEstimator}
     * when location permissions are granted and automatic estimation is
     * enabled.
     */
    public void refreshSunlightEstimate() {
        if (!settingsManager.isAutoSunlightEstimationEnabled()) {
            return;
        }
        boolean hasFine = PermissionUtils.hasPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION);
        boolean hasCoarse = PermissionUtils.hasPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!hasFine && !hasCoarse) {
            return;
        }
        int est = sunlightEstimator.estimateDailySunlightHours();
        if (est > 0) {
            hoursLiveData.setValue(est);
            settingsManager.setLightDuration(est);
            updateDLI(getPPFDValue(), est);
        }
    }

    /**
     * Persist the current measurement values as a diary entry for the given plant.
     */
    public void saveMeasurementEntry(String plantId) {
        String note = String.format(java.util.Locale.US,
                "lux=%.1f ppfd=%.1f dli=%.2f hours=%d",
                getLuxValue(), getPPFDValue(), getDliValue(), getHoursValue());
        DiaryEntry entry = new DiaryEntry(
                java.util.UUID.randomUUID().toString(),
                plantId,
                System.currentTimeMillis(),
                note,
                "",
                "light"
        );
        diaryRepository.insert(entry);
    }
}
