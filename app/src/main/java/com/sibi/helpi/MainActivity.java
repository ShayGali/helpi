package com.sibi.helpi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {
    public static final String USER_COLLECTION = "users";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Customize the BottomNavigationView menu items
        customizeBottomNavigationView(bottomNavigationView);

        // Hide BottomNavigationView for specific fragments
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFragment || destination.getId() == R.id.registerFragment) {
                bottomNavigationView.setVisibility(View.GONE);
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void customizeBottomNavigationView(BottomNavigationView bottomNavigationView) {
        for (int i = 0; i < bottomNavigationView.getMenu().size(); i++) {
            MenuItem menuItem = bottomNavigationView.getMenu().getItem(i);

            // Inflate custom layout for each menu item
            View customView = LayoutInflater.from(this).inflate(R.layout.custom_bottom_navigation_item, bottomNavigationView, false);

            // Set icon and text in the custom layout
            ImageView icon = customView.findViewById(R.id.icon);
            TextView text = customView.findViewById(R.id.text);
            icon.setImageDrawable(menuItem.getIcon());
            text.setText(menuItem.getTitle());

            // Attach the custom view to the menu item
            menuItem.setActionView(customView);
        }
    }
}
