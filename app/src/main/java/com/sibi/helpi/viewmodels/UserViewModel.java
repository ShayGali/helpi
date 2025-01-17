package com.sibi.helpi.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.sibi.helpi.models.User;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {

    // singleton
    private static UserViewModel instance;

    private static final Object lock = new Object();

    public static UserViewModel getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new UserViewModel();
            }
            return instance;
        }
    }

    private UserRepository userRepository;
    private ImagesRepository imagesRepository;

    private UserViewModel() {
        userRepository = new UserRepository();
        imagesRepository = ImagesRepository.getInstance();
    }

    public void registerUserWithEmailAndPassword(User user, String password, byte[] profileImg, @NonNull OnSuccessListener<? super DocumentReference> onSuccess, @NonNull OnFailureListener onFailure) {
        userRepository.registerUserWithEmailAndPassword(user, password, profileImg, onSuccess, onFailure);
    }

    public void authWithGoogle(GoogleSignInAccount account, @NonNull OnSuccessListener<? super DocumentReference> onSuccess, @NonNull OnFailureListener onFailure) {
        userRepository.authWithGoogle(account, onSuccess, onFailure);
    }

    public String getUUID() {
        return userRepository.getUUID();
    }

    public void signOut(GoogleSignInClient googleSignInClient, @NonNull OnCompleteListener<Void> onSuccess) {
        userRepository.signOut(googleSignInClient, onSuccess);
    }

    public LiveData<String> getProfileImage() {
        return imagesRepository.getProfileImage(userRepository.getUUID());
    }

    public LiveData<User> getUser() {
        return userRepository.getUser();
    }

    public void getCurrentUser(@NonNull OnSuccessListener<? super User> onSuccess, @NonNull OnFailureListener onFailure) {
        userRepository.getCurrentUser(onSuccess, onFailure);
    }
}
