package com.sibi.helpi.fragments;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.viewmodels.UserViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileFragment extends Fragment {


    private EditText firstNameInput, lastNameInput, emailInput, phoneNumberInput;
    private static final String TAG = "UpdateProfileFragment";
    private Button changeImageButton, saveButton;

    private Uri profilePicUri;

    private UserViewModel userViewModel;

    private ActivityResultLauncher<String> pickImageLauncher;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        setupViews(view);
        setupObservers();
        setupButtons();



        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            assert data != null;
            profilePicUri = UCrop.getOutput(data);
            if (profilePicUri != null) {
                CircleImageView profilePic = requireView().findViewById(R.id.profile_image);
                profilePic.setImageURI(null); // Clear the image view
                // refresh the image view to make the image appear
                profilePic.setImageURI(profilePicUri); // Show the cropped image, to refresh the image view


            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            assert data != null;
            Throwable cropError = UCrop.getError(data);
            assert cropError != null;
            Toast.makeText(requireContext(), getString(R.string.cropping_failed) + cropError.getMessage(), Toast.LENGTH_SHORT).show();

        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }



    private void saveUserData() {
        String firstName = firstNameInput.getText().toString();
        String lastName = lastNameInput.getText().toString();
        String email = emailInput.getText().toString();
        String phoneNumber = phoneNumberInput.getText().toString();

        byte[] profileImage = getProfileImage();

        userViewModel.isEmailTaken(email).observe(getViewLifecycleOwner(), isTaken -> {
            if (isTaken) {
                emailInput.setError("Email is already taken");
            } else {
                userViewModel.updateProfile(firstName, lastName,  phoneNumber,email, profileImage).observe(getViewLifecycleOwner(), success -> {
                    // show loading indicator


                    if (success) {
                        Toast.makeText(requireContext(), R.string.profile_updated_successfully, Toast.LENGTH_SHORT).show();
                        // Navigate back to the profile fragment
                        Navigation.findNavController(requireView()).navigate(R.id.action_fragment_edit_profile_to_fragment_home);
                    } else {
                        Toast.makeText(requireContext(), R.string.profile_update_failed, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




    }

    private void openImagePicker() {
        // Open image picker
        changeImageButton.setOnClickListener(v -> {
            pickImageLauncher.launch("image/*");
        });
    }



    private void setupViews(View view) {
        firstNameInput = view.findViewById(R.id.first_name_input);
        lastNameInput = view.findViewById(R.id.last_name_input);
        emailInput = view.findViewById(R.id.email_input);
        phoneNumberInput = view.findViewById(R.id.phone_number_input);
        changeImageButton = view.findViewById(R.id.change_image_button);
        saveButton = view.findViewById(R.id.save_button);
    }

    private void setupButtons() {
        changeImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void setupObservers() {

        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
                Log.d(TAG, "State: Loading");
            } else {
                hideLoading();
            }
        });

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(emailInput, saveButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });

        phoneNumberInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePhone(phoneNumberInput, saveButton);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing
            }
        });



    }

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


    private void validateEmail(EditText emailEditText, Button registerButton) {
        String email = emailEditText.getText().toString().trim();
        boolean isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || email.isEmpty();
        registerButton.setEnabled(isEmailValid);
        if (!isEmailValid) {
            emailEditText.setError("Invalid email address");
        }

    }
    private void validatePhone(EditText phoneEditText, Button registerButton) {
        String phone = phoneEditText.getText().toString().trim();
        boolean isPhoneValid = android.util.Patterns.PHONE.matcher(phone).matches() || phone.isEmpty();
        registerButton.setEnabled(isPhoneValid);
        if (!isPhoneValid) {
            phoneEditText.setError("Invalid phone number");
        }
    }

    private void showLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.showProgressBar();
    }

    private void hideLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }



}