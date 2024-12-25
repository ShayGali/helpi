package com.sibi.helpi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
//            Navigation.findNavController(inflate).navigate(R.id.action_login_fragment_to_homeFragment);
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.action_login_fragment_to_homeFragment);
        }

        //TODO: check if the user in sgin in or not (https://firebase.google.com/docs/auth/android/start?hl=he#java)

        Button loginButton = inflate.findViewById(R.id.login_button);
        Button goToRegisterButton = inflate.findViewById(R.id.got_to_reg_button);


        loginButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_homeFragment)
        );

        goToRegisterButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_registerFragment)
        );

        View facebook_button = inflate.findViewById(R.id.facebook_button);
        facebook_button.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_testFragment)
        );

        View Google_button = inflate.findViewById(R.id.google_button);
        Google_button.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_advancedSearchFragment)
        );
        return inflate;
    }

}