package de.omagh.feature_ar;

import android.Manifest;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;
import com.google.ar.sceneform.ux.ArFragment;

import javax.inject.Inject;

import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_infra.ar.ARMeasureOverlay;
import de.omagh.core_infra.ar.HeatmapOverlayView;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.util.PermissionUtils;
import de.omagh.core_infra.measurement.CameraLightMeterX;
import de.omagh.feature_ar.di.DaggerArComponent;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import timber.log.Timber;

/**
 * Activity displaying an AR scene with a heatmap overlay of light intensity.
 */
public class ArHeatmapActivity extends AppCompatActivity {

    @Inject
    MeasurementRepository measurementRepository;

    private ArFragment arFragment;
    private HeatmapOverlayView heatmapView;
    private ARMeasureOverlay arOverlay;
    private CameraLightMeterX cameraMeter;
    private Disposable luxDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreComponent core = ((CoreComponentProvider) getApplicationContext()).getCoreComponent();
        DaggerArComponent.factory().create(core).inject(this);

        if (!checkArSupport()) {
            finish();
            return;
        }
        if (!requestArInstall()) {
            return;
        }

        ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        setupScene();
                    } else {
                        Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                        finish();
                    }
                });

        if (!PermissionUtils.hasPermission(this, Manifest.permission.CAMERA)) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            setupScene();
        }
    }

    private boolean checkArSupport() {
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (!availability.isSupported()) {
            Toast.makeText(this, R.string.device_not_supported, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean requestArInstall() {
        try {
            ArCoreApk.InstallStatus status = ArCoreApk.getInstance().requestInstall(this, true);
            return status != ArCoreApk.InstallStatus.INSTALL_REQUESTED;
        } catch (Exception e) {
            Toast.makeText(this, R.string.device_not_supported, Toast.LENGTH_LONG).show();
            finish();
            return false;
        }
    }

    private void setupScene() {
        setContentView(R.layout.activity_ar_heatmap);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        heatmapView = findViewById(R.id.heatmapOverlay);
        if (arFragment == null) return;

        arOverlay = new ARMeasureOverlay(heatmapView);
        arOverlay.init();

        cameraMeter = new CameraLightMeterX(this, new androidx.camera.view.PreviewView(this));
        cameraMeter.startCamera();
        cameraMeter.analyzeFrameWithGrid(10, 10, new CameraLightMeterX.GridResultCallback() {
            @Override
            public void onResult(float meanR, float meanG, float meanB, float[][] intensity) {
                if (luxDisposable != null && !luxDisposable.isDisposed()) {
                    luxDisposable.dispose();
                }
                luxDisposable = measurementRepository.observeLux()
                        .first(0f)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(lux -> {
                            de.omagh.core_domain.model.Measurement m = new de.omagh.core_domain.model.Measurement();
                            m.lux = lux;
                            m.ppfd = lux; // simplified; conversion handled elsewhere
                            arOverlay.renderOverlay(new android.graphics.Canvas(), m, intensity);
                        }, Timber::e);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(ArHeatmapActivity.this, "Camera error: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (arOverlay != null) arOverlay.cleanup();
        if (cameraMeter != null) cameraMeter.stopCamera();
        if (luxDisposable != null && !luxDisposable.isDisposed()) {
            luxDisposable.dispose();
        }
        super.onDestroy();
    }
}
