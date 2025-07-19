package de.omagh.lumibuddy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import de.omagh.lumibuddy.ui.PrivacyPolicyActivity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;

import de.omagh.lumibuddy.R;
import de.omagh.core_infra.user.SettingsManager;

/**
 * First-run flow that requests runtime permissions and allows anonymous sign-in.
 * Once prerequisites are satisfied, launches {@link MainActivity}.
 */
public class OnboardingActivity extends AppCompatActivity {
    private SettingsManager settingsManager;
    private ActivityResultLauncher<String[]> permissionLauncher;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        settingsManager = new SettingsManager(this);
        auth = FirebaseAuth.getInstance();
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> checkReady());

        Button permButton = findViewById(R.id.requestPermissionsButton);
        Button signInButton = findViewById(R.id.signInButton);
        Button skipButton = findViewById(R.id.skipButton);
        Button privacyButton = findViewById(R.id.privacyPolicyButton);

        permButton.setOnClickListener(v -> requestPermissions());
        signInButton.setOnClickListener(v -> auth.signInAnonymously()
                .addOnCompleteListener(t -> finishOnboarding()));
        skipButton.setOnClickListener(v -> finishOnboarding());
        privacyButton.setOnClickListener(v ->
        startActivity(new Intent(this, PrivacyPolicyActivity.class)));
        
        checkReady();
    }

    private void requestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.BODY_SENSORS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.BODY_SENSORS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
        }
        permissionLauncher.launch(permissions);
    }

    private boolean allPermissionsGranted() {
        boolean storageGranted;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            storageGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                        == PackageManager.PERMISSION_GRANTED &&
                        storageGranted;
    }

    private void checkReady() {
        Button signInButton = findViewById(R.id.signInButton);
        signInButton.setEnabled(allPermissionsGranted());
    }

    private void finishOnboarding() {
        settingsManager.setOnboardingComplete(true);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
