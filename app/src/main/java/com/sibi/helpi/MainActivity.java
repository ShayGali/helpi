package com.sibi.helpi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        progressBar = findViewById(R.id.global_progress_bar);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Customize the BottomNavigationView menu items
        customizeBottomNavigationView(bottomNavigationView);

        // Hide BottomNavigationView for specific fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment
                    || destination.getId() == R.id.registerFragment
                    || destination.getId() == R.id.chatMessagesFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeFirebase() {
        // Initialize Firebase
        FirebaseApp.initializeApp(this);

        // Enable Firestore offline persistence
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);

        // Initialize Realtime Database with correct URL
        try {

            String databaseUrl = "https://helpi-90503-default-rtdb.europe-west1.firebasedatabase.app";
            FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
            database.setPersistenceEnabled(true);

            // Test connection
            database.getReference().child("test").setValue("test_connection")
                    .addOnSuccessListener(aVoid ->
                            Log.d("MainActivity", "Successfully connected to Realtime Database")
                    )
                    .addOnFailureListener(e ->
                            Log.e("MainActivity", "Failed to connect to Realtime Database", e)
                    );

        } catch (Exception e) {
            Log.e("MainActivity", "Error initializing Firebase", e);
        }


    }

    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void customizeBottomNavigationView(BottomNavigationView bottomNavigationView) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
//            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);
//
//            // Inflate custom layout for each menu item
//            View customView = LayoutInflater.from(this).inflate(R.layout.custom_bottom_navigation_item, bottomNavigationView, false);
//
//            // Set icon and text in the custom layout
//            ImageView icon = customView.findViewById(R.id.icon);
//            TextView text = customView.findViewById(R.id.text);
//            icon.setImageDrawable(menuItem.getIcon());
//            text.setText(menuItem.getTitle());
//
//            // Attach the custom view to the menu item
//            menuItem.setActionView(customView);
        }
    }
}
