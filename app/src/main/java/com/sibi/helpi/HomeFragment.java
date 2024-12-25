package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

public class HomeFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_home, container, false);
        View homeButton = inflate.findViewById(R.id.home_btn);
        homeButton.setOnClickListener(v -> {
                    //TODO: remove this:
                    mAuth.signOut();
                    Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_login_fragment);
                }
        );
        return inflate;
    }
}