package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_register, container, false);
        EditText fNameEditText = inflate.findViewById(R.id.first_name_input_reg);
        EditText lNameEditText = inflate.findViewById(R.id.last_name_input_reg);
        EditText emailEditText = inflate.findViewById(R.id.email);
        EditText passwordEditText = inflate.findViewById(R.id.password_input);

        Button registerButton = inflate.findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            // get this user's input
            String fName = fNameEditText.getText().toString();
            String lName = lNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            EditText firstToFocus = null;
            // check if the user has entered all the required fields
            if (fName.isEmpty()) {
                fNameEditText.setError("First name is required");
                firstToFocus = fNameEditText;
            }

            if (lName.isEmpty()) {
                lNameEditText.setError("Last name is required");
                if (firstToFocus == null) firstToFocus = lNameEditText;
            }

            if (email.isEmpty()) {
                emailEditText.setError("Email is required");
                if (firstToFocus == null) firstToFocus = emailEditText;
            }

            if (password.isEmpty()) { // TODO: check if the password is strong enough
                passwordEditText.setError("Password is required");
                if (firstToFocus == null) firstToFocus = passwordEditText;
            }

            if (firstToFocus != null) {
                firstToFocus.requestFocus();
            }


        });

        return inflate;
    }

}