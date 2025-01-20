package com.sibi.helpi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.sibi.helpi.R;
import com.sibi.helpi.viewmodels.UserViewModel;

public class ProfileFragment extends Fragment {

    private UserViewModel userViewModel;
    private GoogleSignInClient googleSignInClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = UserViewModel.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        View backHomeButton = view.findViewById(R.id.back_home_btn);
        backHomeButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_homeFragment);
        });

        View logoutButton = view.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(v -> {
            userViewModel.signOut(googleSignInClient, task -> {
                        Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_loginFragment);
                    }
            );
        });

        TextView usernameTextView = view.findViewById(R.id.username_profile_fragment);
        TextView emailTextView = view.findViewById(R.id.email_profile_fragment);

        // get the user data from the view model
        userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                usernameTextView.setText(user.getUsername());
                emailTextView.setText(user.getEmail());
            }
        });

        ImageView profileImage = view.findViewById(R.id.profile_img_profile_frag);

        // put the profile image in the image view
        userViewModel.getProfileImage().observe(getViewLifecycleOwner(), profileImg -> {
            if (profileImg != null) {
                Glide.with(requireContext())
                        .load(profileImg)
                        .into(profileImage);
            }
        });

        return view;
    }
}