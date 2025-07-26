package de.omagh.lumibuddy.ui;

import android.os.Bundle;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jspecify.annotations.Nullable;

import de.omagh.feature_plantdb.ui.PlantDetailFragment;
import de.omagh.lumibuddy.R;
import de.omagh.lumibuddy.LumiBuddyApplication;
import de.omagh.lumibuddy.di.AppComponent;
import de.omagh.core_infra.user.SettingsManager;
import de.omagh.feature_measurement.ui.SettingsHost;

public class MainActivity extends AppCompatActivity implements SettingsHost {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsManager settings = new SettingsManager(this);
        if (!settings.isOnboardingComplete()) {
            startActivity(new Intent(this, OnboardingActivity.class));
            finish();
            return;
        }
        AppComponent appComponent = ((LumiBuddyApplication) getApplication()).getAppComponent();
        appComponent.inject(this);
        setContentView(R.layout.activity_main);

        // Find the NavHostFragment and NavController
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        // Setup BottomNavigationView with NavController
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigationView);
        NavigationUI.setupWithNavController(bottomNav, navController);
        bottomNav.setOnItemReselectedListener(item ->
                navController.popBackStack(item.getItemId(), false));

        if (getIntent() != null && getIntent().hasExtra("openPlantId")) {
            Bundle args = new Bundle();
            args.putString(PlantDetailFragment.ARG_ID, getIntent().getStringExtra("openPlantId"));
            args.putString(PlantDetailFragment.ARG_NAME, getIntent().getStringExtra("openPlantName"));
            args.putString(PlantDetailFragment.ARG_TYPE, getIntent().getStringExtra("openPlantType"));
            args.putString(PlantDetailFragment.ARG_IMAGE_URI, getIntent().getStringExtra("openPlantImageUri"));
            navController.navigate(R.id.plantDetailFragment, args);
        }

    }

    @Override
    public void onShowPrivacyPolicy() {
        startActivity(new Intent(this, PrivacyPolicyActivity.class));
    }
}
