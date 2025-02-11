package com.sibi.helpi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.models.User;
import com.sibi.helpi.viewmodels.UserViewModel;

public class ProfileFragment extends Fragment {
    private UserViewModel userViewModel;
    private GoogleSignInClient googleSignInClient;

    // Views
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView profileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        setupClickListeners(view);
        setupObservers();

        return view;
    }

    private void initializeViews(View view) {
        usernameTextView = view.findViewById(R.id.username_profile_fragment);
        emailTextView = view.findViewById(R.id.email_profile_fragment);
        profileImage = view.findViewById(R.id.profile_img_profile_frag);
    }

    private void setupClickListeners(View view) {
        // Back button
        view.findViewById(R.id.back_home_btn).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_homeFragment)
        );

        // Logout button
        view.findViewById(R.id.logout_btn).setOnClickListener(v -> {
            showLoading();
            userViewModel.signOut(googleSignInClient);
        });
    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
            } else if (state.getError() != null) {
                hideLoading();
                Toast.makeText(requireContext(), state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoading();
                updateUserInfo(state.getUser());
            } else if (state.getUser() == null) { // successfully signed out
                hideLoading();
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_loginFragment);
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

    private void updateUserInfo(User user) {
//        Toast.makeText(requireContext(), "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();  Toast is not needed
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
        updateProfileImage(user.getProfileImgUri());
    }

    private void updateProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(profileImage);
        }
        else {
            profileImage.setImageResource(R.drawable.icon_account_circle);
        }
    }

}