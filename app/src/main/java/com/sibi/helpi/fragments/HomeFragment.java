package com.sibi.helpi.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.viewmodels.UserViewModel;

public class HomeFragment extends Fragment implements OnMapReadyCallback {
    private MapView mapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private UserViewModel userViewModel;
    private TextView usernameTextView;
    private ImageView profileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        usernameTextView = view.findViewById(R.id.userNameTextView);
        profileImage = view.findViewById(R.id.profile_img);

        setupViews(view);
        setupObservers();

        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        return view;
    }

    private void setupViews(View view) {
        profileImage.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profileFragment)
        );
    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
                Log.d("HomeFragment", "State: Loading");
            } else if (state.getError() != null) {
                hideLoading();
                Log.e("HomeFragment", "State: Error - " + state.getError());
                usernameTextView.setText("Welcome, Guest");
                // Optionally show error to user
                // Toast.makeText(requireContext(), state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoading();
                Log.d("HomeFragment", "State: Success - User: " + state.getUser().getEmail());
                String name = "Hi, " + state.getUser().getFirstName() +
                        " " + state.getUser().getLastName() + "!";
                usernameTextView.setText(name);
            } else {
                hideLoading();
                Log.w("HomeFragment", "State: Unknown - neither loading, error, nor user");
                usernameTextView.setText("Welcome, Guest");
            }
        });

        // Observe profile image changes
        userViewModel.getProfileImage().observe(getViewLifecycleOwner(), imageUrl -> {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Load image using your preferred image loading library
                // For example, using Glide:
                Glide.with(this)
                        .load(imageUrl)
                        .into(profileImage);
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        googleMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void showLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.showProgressBar();
    }

    private void hideLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }
}