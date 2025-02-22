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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;


import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

    EditText fNameEditText;
    EditText lNameEditText;
    EditText emailEditText;
    EditText phoneEditText;
    EditText passwordEditText;
    Button registerButton;
    Button chooseProfilePicButton;
    View googleButton;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
//                        String fileName = "cropped_image_ " + System.currentTimeMillis() + ".jpg"; //another option to create a unique file name
                        String fileName = "cropped_image.jpg";
                        Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), fileName));
                        UCrop.of(uri, destinationUri)
                                .withAspectRatio(1, 1) // Square aspect ratio
                                .withMaxResultSize(500, 500) // Adjust resolution
                                .start(requireContext(), this);
                    }
                }
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_register, container, false);

        setupViews(inflate);
        setupObservers();
        setupButtons();

        return inflate;
    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                // Show loading indicator
                showLoadingIndicator();
            } else if (state.getError() != null) {
                // Hide loading and show error
                hideLoadingIndicator();
                Log.w(TAG, "Authentication failed: " + state.getError());
                emailEditText.setError(state.getError());
                emailEditText.requestFocus();
//

            } else if (state.getUser() != null) {
                // Registration successful, navigate to next screen
                hideLoadingIndicator();
                Log.d(TAG, "Authentication successful");
                Toast.makeText(requireContext(), "User created successfully", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_registerFragment_to_homeFragment);
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(emailEditText, registerButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone(phoneEditText, registerButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });
    }

    private void setupViews(View view) {
        fNameEditText = view.findViewById(R.id.first_name_input_reg);
        lNameEditText = view.findViewById(R.id.last_name_input_reg);
        emailEditText = view.findViewById(R.id.email);
        phoneEditText = view.findViewById(R.id.phone_input);
        passwordEditText = view.findViewById(R.id.password_input);
        registerButton = view.findViewById(R.id.register_button);
        chooseProfilePicButton = view.findViewById(R.id.choose_picture_button);
        googleButton = view.findViewById(R.id.google_button);
    }


    private void setupButtons() {
        chooseProfilePicButton.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });

        registerButton.setOnClickListener(v -> {
            String fName = fNameEditText.getText().toString();
            String lName = lNameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phone = phoneEditText.getText().toString();
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
            User user = new User(email, fName, lName, phone, null);
            byte[] profileImg = getProfileImage();

            userViewModel.registerUser(user, password, profileImg);




        });

        googleButton.setOnClickListener(v -> {
            signInWithGoogle();
        });

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
                profilePic.setImageURI(null); // Clear the image view
                // refresh the image view to make the image appear
                profilePic.setImageURI(profilePicUri); // Show the cropped image, to refresh the image view


            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Throwable cropError = UCrop.getError(data);
            Toast.makeText(requireContext(), "Cropping failed: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        } else if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                GoogleSignInAccount account = task.getResult();
                if (account != null) {
                    // Call the new authWithGoogle method
                    userViewModel.authWithGoogle(account);
                }
            } else {
                // Handle sign-in failure
                Log.w(TAG, "Google sign-in failed", task.getException());
                Toast.makeText(requireContext(), "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    private void hideLoadingIndicator() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }

    private void showLoadingIndicator() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }

    private void validateEmail(EditText emailEditText, Button registerButton) {
        String email = emailEditText.getText().toString().trim();
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        registerButton.setEnabled(isEmailValid);
        if (!isEmailValid) {
            emailEditText.setError("Invalid email address");
        }
    }
    private void validatePhone(EditText phoneEditText, Button registerButton) {
        String phone = phoneEditText.getText().toString().trim();
        boolean isPhoneValid = android.util.Patterns.PHONE.matcher(phone).matches();
        registerButton.setEnabled(isPhoneValid);
        if (!isPhoneValid) {
            phoneEditText.setError("Invalid phone number");
        }
    }
}