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
import android.widget.Button;
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
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.viewmodels.ProductViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

public class HomeFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private UserViewModel userViewModel;
    private ProductViewModel productViewModel;
    private TextView usernameTextView;
    private ImageView profileImage;

    private Button goToAdminDashboardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        productViewModel = new ViewModelProvider(requireActivity()).get(ProductViewModel.class);
        userViewModel.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        usernameTextView = view.findViewById(R.id.userNameTextView);
        profileImage = view.findViewById(R.id.profile_img);
        goToAdminDashboardButton = view.findViewById(R.id.goToAdminDashboardButton);

        setupViews(view);
        setupObservers();

        return view;
    }

    private void setupViews(View view) {
        profileImage.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profileFragment)
        );
        goToAdminDashboardButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_adminDashBoardFragment)
        );

        goToAdminDashboardButton.setVisibility(View.GONE);

    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
                Log.d("HomeFragment", "State: Loading");
            } else if (state.getError() != null) {
                hideLoading();
                Log.e("HomeFragment", "State: Error - " + state.getError());
                usernameTextView.setText(R.string.welcome_guest);
                // Optionally show error to user
                // Toast.makeText(requireContext(), state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoading();
                Log.d("HomeFragment", "State: Success - User: " + state.getUser().getEmail());
                String helloMsg = getString(R.string.hi) + ", " + state.getUser().getFirstName() +
                        " " + state.getUser().getLastName() + "!";
                usernameTextView.setText(helloMsg);

                String imageUrl = state.getUser().getProfileImgUri();
                Log.d("HomeFragment", "Profile image URL: " + imageUrl);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.icon_account_circle);
                }

                if (state.getUser() != null && state.getUser().isAdmin()) {
                    goToAdminDashboardButton.setVisibility(View.VISIBLE);
                } else {
                    goToAdminDashboardButton.setVisibility(View.GONE);
                }

            } else {
                hideLoading();
                Log.w("HomeFragment", "State: Unknown - neither loading, error, nor user");
                usernameTextView.setText(R.string.welcome_guest);
            }
        });
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