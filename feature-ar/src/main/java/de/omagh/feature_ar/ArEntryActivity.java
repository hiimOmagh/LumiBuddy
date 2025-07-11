package de.omagh.feature_ar;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.ArCoreApk;

import org.jspecify.annotations.Nullable;

public class ArEntryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_entry);

        TextView statusView = findViewById(R.id.arStatus);
        ArCoreApk.Availability availability = ArCoreApk.getInstance().checkAvailability(this);
        if (!availability.isSupported()) {
            Toast.makeText(this, R.string.device_not_supported, Toast.LENGTH_LONG).show();
            statusView.setText(R.string.device_not_supported);
        }
    }
}