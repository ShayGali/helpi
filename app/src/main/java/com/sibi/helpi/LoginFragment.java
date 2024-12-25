package com.sibi.helpi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

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

        // initialize the views
        emailEditText = inflate.findViewById(R.id.login_email_input);
        passwordEditText = inflate.findViewById(R.id.password_input);
        loginButton = inflate.findViewById(R.id.login_button);
        initSignInProcess();


        //TODO: check if the user in sgin in or not (https://firebase.google.com/docs/auth/android/start?hl=he#java)

        Button goToRegisterButton = inflate.findViewById(R.id.got_to_reg_button);


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

    private void initSignInProcess() {
        loginButton.setOnClickListener(v -> {
                    // get the email and password from the views
                    String email = emailEditText.getText().toString();
                    String password = passwordEditText.getText().toString();

                    // check if the email and password are not empty
                    if (email.isEmpty() || password.isEmpty()) {
                        // show an error message
                        Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // sign in the user
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // navigate to the home fragment
                                    Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_homeFragment);
                                } else {
                                    // show an error message
                                    Toast.makeText(requireContext(), "Failed to sign in", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        );
    }

}