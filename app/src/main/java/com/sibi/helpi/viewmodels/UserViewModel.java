package com.sibi.helpi.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.sibi.helpi.models.User;
import com.sibi.helpi.repositories.UserRepository;

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

    public UserViewModel() {
        userRepository = new UserRepository();
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
}
