package com.sibi.helpi.fragments;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.viewmodels.UserViewModel;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            navigateToHome();
        }

        emailEditText = view.findViewById(R.id.login_email_input);
        passwordEditText = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> signIn());

        Button goToRegisterButton = view.findViewById(R.id.go_to_reg_button);
        goToRegisterButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_registerFragment)
        );

        View googleButton = view.findViewById(R.id.google_button);
        googleButton.setOnClickListener(v -> signInWithGoogle());

        setupObservers(view);
    }

    private void setupObservers(View inflater) {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoadingIndicator();
            } else if (state.getError() != null) {
                hideLoadingIndicator();
                TextView errorTextView = inflater.findViewById(R.id.error_on_login_msg);
                errorTextView.setText(state.getError());
                Log.w(TAG, "Authentication failed: " + state.getError());
                Toast.makeText(requireContext(), "Authentication failed: " + state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoadingIndicator();
                Log.d(TAG, "Authentication successful");
                Toast.makeText(requireContext(), "User signed in successfully", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    userViewModel.authWithGoogle(account);
                }
            } else {
                Toast.makeText(requireContext(), "Failed to sign in with google", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "register with google:failure", task.getException());
            }
        }
    }

    private void signIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.signInWithEmail(email, password);
    }

    private void navigateToHome() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();
        Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_homeFragment, null, navOptions);
    }

    private void hideLoadingIndicator() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }

    private void showLoadingIndicator() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }
}