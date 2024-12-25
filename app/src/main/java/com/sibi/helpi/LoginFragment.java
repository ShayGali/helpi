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

public class LoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO: check if the user in sgin in or not (https://firebase.google.com/docs/auth/android/start?hl=he#java)

        Button loginButton = view.findViewById(R.id.login_button);
        Button goToRegisterButton = view.findViewById(R.id.got_to_reg_button);


        loginButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_homeFragment)
        );

        goToRegisterButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_registerFragment)
        );

        View facebook_button = view.findViewById(R.id.facebook_button);
        facebook_button.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_testFragment)
        );
    }
}