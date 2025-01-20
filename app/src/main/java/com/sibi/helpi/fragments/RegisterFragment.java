package com.sibi.helpi.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;


import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.models.User;
import com.sibi.helpi.viewmodels.UserViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;


public class RegisterFragment extends Fragment {
    private static final int RC_SIGN_IN = 1234;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<Intent> cropImageLauncher;
    private UserViewModel userViewModel;

    private Uri profilePicUri;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

        userViewModel = UserViewModel.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped_image.jpg"));
                        UCrop.of(uri, destinationUri)
                                .withAspectRatio(1, 1) // Square aspect ratio
                                .withMaxResultSize(500, 500) // Adjust resolution
                                .start(requireContext(), this);
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

            // Create user with email and password
            User user = new User(email, fName, lName, "", null);
            byte[] profileImg = getProfileImage();
            userViewModel.registerUserWithEmailAndPassword(user, password, profileImg,
                    documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_homeFragment);
                    },
                    e -> {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Authentication failed." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

        View googleButton = inflate.findViewById(R.id.google_button);
        googleButton.setOnClickListener(v -> {
            signInWithGoogle();
        });
        return inflate;
    }

    /**
     * Get the profile image from the uri that was selected by the user
     *
     * @return byte array of the image (can be null if the uri is null)
     */
    @Nullable
    private byte[] getProfileImage() {
        if (profilePicUri == null) {
            return null;
        }

        try {
            Bitmap compressedImage = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), profilePicUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            profilePicUri = UCrop.getOutput(data);
            if (profilePicUri != null) {
                CircleImageView profilePic = requireView().findViewById(R.id.reg_profile_picture);
                profilePic.setImageURI(profilePicUri); // Show the cropped image
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(requireContext(), "Cropping failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    userViewModel.authWithGoogle(account,
                            documentReference -> {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(getContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_homeFragment);
                            },
                            e -> {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(getContext(), "Authentication failed." + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }
            } else {
                // Handle error
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }


//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
//                        if (isNewUser) {
//                            // Save user to database
//                            saveUserDatabase(account.getGivenName(), account.getFamilyName(), account.getEmail(), "", account.getPhotoUrl());
//                        }
//                        Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_homeFragment);
//                    } else {
//                        Log.w(TAG, "register with google:failure", task.getException());
//                        Toast.makeText(requireContext(), "Failed to sign in with google", Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

//    private void saveUserDatabase(String fName, String lName, String email, String phoneNumber, Uri profilePicUri) {
//        // Save user to database
//        db.collection(MainActivity.USER_COLLECTION)
//                .add(new User(fName, lName, email, phoneNumber, "path"))
//                .addOnSuccessListener(documentReference -> {
//                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
//                })
//                .addOnFailureListener(e -> {
//                    Log.w(TAG, "Error adding document", e);
//                });
//    }
}