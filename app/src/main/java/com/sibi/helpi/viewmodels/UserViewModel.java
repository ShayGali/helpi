package com.sibi.helpi.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.sibi.helpi.models.User;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.UserRepository;
import com.sibi.helpi.stats.UserState;
import com.sibi.helpi.utils.AppConstants;

import java.util.Map;
import java.util.Objects;

public class UserViewModel extends ViewModel {
    private static final String TAG = "UserViewModel";
    private final MutableLiveData<UserState> userState;
    private boolean isInitialized;
    private final UserRepository userRepository;
    private final ImagesRepository imagesRepository;
    private static UserViewModel instance;
    private final MutableLiveData<Boolean> hasUnreadMessages = new MutableLiveData<>(false);

    public UserViewModel() {
        userRepository = new UserRepository();
        imagesRepository = ImagesRepository.getInstance();
        userState = new MutableLiveData<>();
        userState.postValue(UserState.idle());
        isInitialized = false;
    }

    public LiveData<UserState> getUserState() {
        if (!isInitialized) {
            try {
                getCurrentUser();
                isInitialized = true;
            } catch (IllegalStateException ignored) {
            }
        }
        return userState;
    }

    public void registerUser(User user, String password, byte[] profileImg) {
        userState.setValue(UserState.loading());
        userRepository.registerUser(user, password, profileImg)
                .addOnSuccessListener(documentRef -> userState.setValue(UserState.success(user)))
                .addOnFailureListener(e -> userState.setValue(UserState.error(e.getMessage())));
    }

    public void authWithGoogle(GoogleSignInAccount account) {
        userState.setValue(UserState.loading());
        userRepository.authWithGoogle(account)
                .addOnSuccessListener(documentRef -> getCurrentUser())
                .addOnFailureListener(e -> userState.setValue(UserState.error(e.getMessage())));
    }

    public void getCurrentUser() {
        UserState currentState = userState.getValue();
        if (currentState != null && currentState.getUser() != null) {
            Log.d(TAG, "getCurrentUser: User already in state, returning");
            return;
        }

        Log.d(TAG, "getCurrentUser: Fetching user data...");
        userState.setValue(UserState.loading());

        userRepository.getCurrentUser()
                .addOnSuccessListener(user -> {
                    Log.d(TAG, "getCurrentUser: Success - User: " + (user != null ? user.getEmail() : "null"));
                    if (user != null) {
                        userState.setValue(UserState.success(user));
                        isInitialized = true;
                    } else {
                        userState.setValue(UserState.error("User data is null"));
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "getCurrentUser: Error", e);
                    userState.setValue(UserState.error(e.getMessage()));
                });
    }

    public void signOut(GoogleSignInClient googleSignInClient) {
        userState.postValue(UserState.loading());
        userRepository.signOut(googleSignInClient)
                .addOnSuccessListener(aVoid -> {
                    userState.postValue(UserState.success(null));
                    instance = null;
                })
                .addOnFailureListener(e -> userState.postValue(UserState.error(e.getMessage())));
    }

    public LiveData<String> getProfileImageUri() {
        if (!isInitialized || Objects.requireNonNull(userState.getValue()).isIdle()) {
            throw new IllegalStateException("User not initialized - cant get profile image");
        }

        return imagesRepository.getProfileImage(userRepository.getUUID());
    }

    public String getCurrentUserId() {
        return userRepository.getUUID();
    }

    public void signInWithEmail(String email, String password) {
        userState.setValue(UserState.loading());
        userRepository.signInWithEmail(email, password)
                .addOnSuccessListener(documentRef -> getCurrentUser())
                .addOnFailureListener(e -> userState.setValue(UserState.error(e.getMessage())));
    }

    public void deleteAccount() {
        userState.setValue(UserState.loading());
        userRepository.deleteAccount()
                .addOnSuccessListener(aVoid -> {
                    userState.setValue(UserState.success(null));
                    instance = null;
                })
                .addOnFailureListener(e -> userState.setValue(UserState.error(e.getMessage())));
    }

    public LiveData<User> getUserByIdLiveData(String userId) {
        return userRepository.getUserByIdLiveData(userId);
    }


    public LiveData<Boolean> updateProfile(String firstName, String lastName, String phoneNumber, String email, byte[] profileImg) {
        MutableLiveData<Boolean> successLiveData = new MutableLiveData<>();
        userState.setValue(UserState.loading());
        userRepository.updateProfile(firstName, lastName, phoneNumber, email, profileImg)
                .addOnSuccessListener(aVoid -> {
                    userState.setValue(UserState.success(null));
                    successLiveData.postValue(true);
                })
                .addOnFailureListener(e -> {
                    userState.setValue(UserState.error(e.getMessage()));
                    successLiveData.postValue(false);
                });

        return successLiveData;
    }

    public LiveData<Boolean> isEmailTaken(String email) {
        MutableLiveData<Boolean> isTakenLiveData = new MutableLiveData<>();
        userRepository.isEmailTaken(email)
                .addOnSuccessListener(isTakenLiveData::postValue)
                .addOnFailureListener(e -> isTakenLiveData.postValue(true));

        return isTakenLiveData;
    }



    public LiveData<Boolean> getNotificationSetting() {
        MutableLiveData<Boolean> notificationSetting = new MutableLiveData<>();
        userRepository.getUserNotificationStatus(getCurrentUserId()).addOnSuccessListener(notificationSetting::postValue)
                .addOnFailureListener(e -> notificationSetting.postValue(false));
        return notificationSetting;
      }

      public LiveData<Boolean> setNotificationSetting(boolean isEnabled) {
        MutableLiveData<Boolean> notificationSetting = new MutableLiveData<>();
        userRepository.updateUserNotificationStatus(getCurrentUserId(), isEnabled)
                .addOnSuccessListener(aVoid -> notificationSetting.postValue(true))
                .addOnFailureListener(e -> notificationSetting.postValue(false));
        return notificationSetting;
        }












}