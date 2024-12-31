package com.sibi.helpi;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.Manifest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.io.File;


public class RegisterFragment extends Fragment {
    private static final int RC_SIGN_IN = 1234;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // puut the image in the image view
                        ImageView imageView = requireView().findViewById(R.id.reg_profile_picture);
                        imageView.setImageURI(uri);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_register, container, false);

        EditText fNameEditText = inflate.findViewById(R.id.first_name_input_reg);
        EditText lNameEditText = inflate.findViewById(R.id.last_name_input_reg);
        EditText emailEditText = inflate.findViewById(R.id.email);
        EditText passwordEditText = inflate.findViewById(R.id.password_input);

        Button registerButton = inflate.findViewById(R.id.register_button);
        Button chooseProfilePicButton = inflate.findViewById(R.id.choose_picture_button);

        chooseProfilePicButton.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        registerButton.setOnClickListener(v -> {
            String fName = fNameEditText.getText().toString();
            String lName = lNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            EditText firstToFocus = null;
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

            if (password.isEmpty()) {
                passwordEditText.setError("Password is required");
                if (firstToFocus == null) firstToFocus = passwordEditText;
            }

            if (firstToFocus != null) {
                firstToFocus.requestFocus();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), "Authentication failed." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        View googleButton = inflate.findViewById(R.id.google_button);
        googleButton.setOnClickListener(v -> {
            signInWithGoogle();
        });
        return inflate;
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
            }
        }
//        } else if (requestCode == UCrop.REQUEST_CROP) {
//            cropImageLauncher.launch(data);
//        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_homeFragment);
                    } else {
                        Log.w(TAG, "register with google:failure", task.getException());
                        Toast.makeText(requireContext(), "Failed to sign in with google", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}