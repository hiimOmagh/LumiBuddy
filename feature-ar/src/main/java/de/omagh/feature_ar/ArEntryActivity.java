package de.omagh.feature_ar;

import android.Manifest;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.util.Locale;

import javax.inject.Inject;

import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_data.model.DiaryEntry;
import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_infra.di.CoreComponent;
import de.omagh.core_infra.di.CoreComponentProvider;
import de.omagh.core_infra.util.PermissionUtils;
import de.omagh.core_infra.ar.ARGrowthTracker;
import de.omagh.core_infra.ar.ARCoreGrowthTracker;
import de.omagh.feature_ar.di.DaggerArComponent;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

/**
 * Activity displaying an AR scene that places light measurement markers on tapped planes.
 */
public class ArEntryActivity extends AppCompatActivity {

    public static final String EXTRA_PLANT_ID = "plant_id";

    @Inject
    MeasurementRepository measurementRepository;
    @Inject
    DiaryRepository diaryRepository;

    private String plantId;

    private ArFragment arFragment;
    private ARGrowthTracker growthTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CoreComponent core = ((CoreComponentProvider) getApplicationContext()).getCoreComponent();
        DaggerArComponent.factory().create(core).inject(this);

        plantId = getIntent().getStringExtra(EXTRA_PLANT_ID);

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
        setContentView(R.layout.activity_ar_entry);
        TextView statusView = findViewById(R.id.arStatus);
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        if (arFragment == null) {
            statusView.setText(R.string.device_not_supported);
            return;
        }
        growthTracker = new ARCoreGrowthTracker(arFragment);
        growthTracker.init();
        arFragment.setOnTapArPlaneListener(this::placeMarker);
    }

    private void placeMarker(HitResult hitResult, com.google.ar.core.Plane plane, android.view.MotionEvent motionEvent) {
        if (growthTracker != null) {
            growthTracker.trackGrowth(null);
        }
        Anchor anchor = hitResult.createAnchor();
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        TextView tv = new TextView(this);
        tv.setTextColor(Color.BLACK);
        tv.setBackgroundColor(Color.argb(200, 255, 255, 255));

        ViewRenderable.builder()
                .setView(this, tv)
                .build()
                .thenAccept(renderable -> {
                    Node node = new Node();
                    node.setParent(anchorNode);
                    node.setRenderable(renderable);
                    measurementRepository.observeLux()
                            .first(0f)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(lux -> {
                                tv.setText(String.format(Locale.US, "%.1f lx", lux));
                                int color = heatmapColor(lux);
                                tv.setBackgroundColor(color);
                                Uri screenshot = captureScreenshot();
                                DiaryEntry entry = new DiaryEntry(
                                        java.util.UUID.randomUUID().toString(),
                                        plantId,
                                        System.currentTimeMillis(),
                                        "",
                                        screenshot != null ? screenshot.toString() : "",
                                        "ar_scan"
                                );
                                diaryRepository.insert(entry);
                                Toast.makeText(this, R.string.add_diary_entry, Toast.LENGTH_SHORT).show();
                            });
                })
                .exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                });
    }

    private int heatmapColor(float val) {
        float scaled = Math.min(1f, val / 1000f);
        int red = (int) (scaled * 255);
        int blue = 255 - red;
        return Color.argb(200, red, 0, blue);
    }

    private Uri captureScreenshot() {
        try {
            android.view.View view = getWindow().getDecorView().getRootView();
            Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            view.draw(canvas);
            java.io.File dir = new java.io.File(getFilesDir(), "images");
            if (!dir.exists()) dir.mkdirs();
            java.io.File file = java.io.File.createTempFile("ar_", ".png", dir);
            try (java.io.FileOutputStream out = new java.io.FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
            }
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        if (growthTracker != null) {
            growthTracker.cleanup();
            growthTracker = null;
        }
        super.onDestroy();
    }
}
