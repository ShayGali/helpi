package com.sibi.helpi.fragments;

import static com.sibi.helpi.utils.LocaleHelper.setLocale;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.models.User;
import com.sibi.helpi.viewmodels.ChatViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

public class ProfileFragment extends Fragment {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String LANGUAGE_KEY = "language";
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;
    private GoogleSignInClient googleSignInClient;

    // Views
    private TextView usernameTextView;
    private TextView emailTextView;
    private ImageView profileImage;
    private View unreadDot;

    private SwitchMaterial notificationSwitch;

    private boolean isNotificationEnabled = true;





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initializeViews(view);
        setupClickListeners(view);
        setupObservers();

        return view;
    }

    private void initializeViews(View view) {
        usernameTextView = view.findViewById(R.id.username_profile_fragment);
        emailTextView = view.findViewById(R.id.email_profile_fragment);
        profileImage = view.findViewById(R.id.profile_img_profile_frag);
        unreadDot = view.findViewById(R.id.unread_dot);
        notificationSwitch = view.findViewById(R.id.switchNotification);

    }

    private void setupClickListeners(View view) {
        // Back button
        view.findViewById(R.id.back_home_btn).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_homeFragment)
        );

        // Chats button
        view.findViewById(R.id.chat_list_btn).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_chatListFragment)
        );

        // Logout button
        view.findViewById(R.id.logout_btn).setOnClickListener(v -> {
            showLoading();
            userViewModel.signOut(googleSignInClient);
        });

        view.findViewById(R.id.deleteAccountBtn).setOnClickListener(v -> {
            showLoading();
            userViewModel.deleteAccount();
        });
        view.findViewById(R.id.updateProfileBtn).setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_profileFragment_to_fragment_edit_profile)
        );


        View changeLanguageBtn = view.findViewById(R.id.changeLanguageBtn);

        changeLanguageBtn.setOnClickListener(v -> {
            // open language dialog
            String[] languages = getResources().getStringArray(R.array.languages);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
                String selectedLanguage = languages[which];
                setLanguage(selectedLanguage);
                getActivity().recreate();
                dialog.dismiss();
            });
            builder.create().show();
        });

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isNotificationEnabled = isChecked;
            userViewModel.setNotificationSetting(isChecked).observe(getViewLifecycleOwner(), aBoolean -> {
                if (aBoolean) {
                    updateSwitchColor(isChecked);
                } else {
                    notificationSwitch.setChecked(!isChecked);
                    isNotificationEnabled = !isChecked;
                    Toast.makeText(requireContext(), "Failed to update notification setting", Toast.LENGTH_SHORT).show();
                }
            });

        });







    }

    private void setLanguage(String selectedLanguage) {
        String languageCode;
        switch (selectedLanguage) {
            case "Hebrew":
            case "עברית":
                languageCode = "he";
                break;
            case "English":
            case "אנגלית":
                languageCode = "en";
                break;
            default:
                languageCode = "en";
                break;
        }

        // Save the selected language to SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, languageCode);
        editor.apply();

        // Update the app locale
        setLocale(requireContext(), languageCode);

        // Force restart the entire app
        Intent intent = new Intent(requireContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
            } else if (state.getError() != null) {
                hideLoading();
                Toast.makeText(requireContext(), state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoading();
                updateUserInfo(state.getUser());
            } else if (state.getUser() == null) { // successfully signed out
                hideLoading();
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_loginFragment);
            }
        });

        chatViewModel.getHasUnreadMessages(userViewModel.getCurrentUserId())
                .observe(getViewLifecycleOwner(), hasUnread -> {
                    if (unreadDot != null) {
                        unreadDot.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
                    }
                });

        userViewModel.getNotificationSetting().observe(getViewLifecycleOwner(), isEnabled -> {
            isNotificationEnabled = isEnabled;
            notificationSwitch.setChecked(isEnabled);
            updateSwitchColor(isEnabled);
        });

    }



    private void showLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.showProgressBar();
    }

    private void hideLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }

    private void updateUserInfo(User user) {
//        Toast.makeText(requireContext(), "Welcome " + user.getUsername(), Toast.LENGTH_SHORT).show();  Toast is not needed
        usernameTextView.setText(user.getUsername());
        emailTextView.setText(user.getEmail());
        updateProfileImage(user.getProfileImgUri());
    }

    private void updateProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(imageUrl)
                    .into(profileImage);
        } else {
            profileImage.setImageResource(R.drawable.icon_account_circle);
        }
    }

    private void updateSwitchColor(boolean isChecked) {
        int thumbColor = isChecked ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.gray);
        int trackColor = isChecked ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.gray);

        notificationSwitch.setThumbTintList(ColorStateList.valueOf(thumbColor));
        notificationSwitch.setTrackTintList(ColorStateList.valueOf(trackColor));
    }







}