package com.sibi.helpi;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginFragment extends Fragment {
    // for google login
    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient googleSignInClient;

    // for firebase auth
    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check if the user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) { // if the user is already signed in
            navigateToHome();
        }

        // initialize the views
        emailEditText = view.findViewById(R.id.login_email_input);
        passwordEditText = view.findViewById(R.id.password_input);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> signIn());

        Button goToRegisterButton = view.findViewById(R.id.got_to_reg_button);
        goToRegisterButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_fragment_to_registerFragment)
        );

        View facebook_button = view.findViewById(R.id.facebook_button);
        View Google_button = view.findViewById(R.id.google_button);
        Google_button.setOnClickListener(v -> signInWithGoogle());
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
                    firebaseAuthWithGoogle(account);
                }
            } else {
                // Handle error
                Toast.makeText(requireContext(), "Failed to sign in with google", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "register with google:failure", task.getException());
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        navigateToHome();
                    } else {
                        // Handle error
                        Log.w(TAG, "register with google:failure", task.getException());
                        Toast.makeText(requireContext(), "Failed to sign in with google", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signIn() {
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
                        navigateToHome();
                    } else {
                        // TODO: show an error message
                        Toast.makeText(requireContext(), "Failed to sign in", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome() {
        NavOptions navOptions = new NavOptions.Builder()
                .setPopUpTo(R.id.loginFragment, true)
                .build();
        Navigation.findNavController(requireView()).navigate(R.id.action_login_fragment_to_homeFragment, null, navOptions);
    }
}