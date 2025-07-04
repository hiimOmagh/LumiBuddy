package de.omagh.lumibuddy.ui;

import android.os.Bundle;

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

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
