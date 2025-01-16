package com.sibi.helpi.repositories;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sibi.helpi.models.User;

public class UserRepository {
    public static final String COLLECTION_NAME = "users";
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;
    private final ImagesRepository imagesRepository;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userCollection = db.collection(COLLECTION_NAME);
        imagesRepository = ImagesRepository.getInstance();
    }


    /**
     * Create a new user with a given email and password
     *
     * @param user      user object
     * @param password  user password
     * @param onSuccess callback when success
     * @param onFailure callback when failure
     */

    public void registerUserWithEmailAndPassword(User user, String password, byte[] profileImg, @NonNull OnSuccessListener<? super DocumentReference> onSuccess, @NonNull OnFailureListener onFailure) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //TODO - upload image to storage and get the path
                        saveUserData(user, profileImg, onSuccess);
                    } else {
                        if (task.getException() != null)
                            onFailure.onFailure(task.getException());
                        else {
                            onFailure.onFailure(new Exception("Failed to create user for unknown reason"));
                        }
                    }
                });

    }


    /**
     * This function save new user to firestore with image
     *
     * @param user       user object
     * @param onSuccess  callback when success (return document reference)
     * @param profileImg user profile image
     */
    public void saveUserData(User user, byte[] profileImg, @NonNull OnSuccessListener<? super DocumentReference> onSuccess) {
        if (profileImg == null) {
            user.setProfileImgPath("");
            saveUserData(user, onSuccess);
            return;
        }
        userCollection.add(user).addOnSuccessListener(documentReference -> {
            imagesRepository.uploadProfileImage(documentReference.getId(), profileImg)
                    .addOnSuccessListener(uri -> {
                        userCollection.document(documentReference.getId()).update("profileImgPath", uri.toString())
                                .addOnSuccessListener(aVoid -> onSuccess.onSuccess(documentReference))
                                .addOnFailureListener(e -> Log.e("UserRepository", "Failed to update profile image path: " + e));
                    })
                    .addOnFailureListener(e -> Log.e("UserRepository", "Failed to upload profile image: " + e));
        }).addOnFailureListener(e -> Log.e("UserRepository", "Failed to save user data: " + e));
    }


    /**
     * save user data to firestore without image
     *
     * @param user      user object
     * @param onSuccess callback when success (return document reference)
     */
    public void saveUserData(User user, @NonNull OnSuccessListener<? super DocumentReference> onSuccess) {
        userCollection.add(user).addOnSuccessListener(onSuccess);
    }

    public void authWithGoogle(GoogleSignInAccount account, @NonNull OnSuccessListener<? super DocumentReference> onSuccess, @NonNull OnFailureListener onFailure) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                        if (isNewUser) {
                            User user = new User(account.getGivenName(), account.getFamilyName(), account.getEmail(), "", account.getPhotoUrl().toString());
                            // get image  from google account
                            saveUserData(user, onSuccess);
                        }
                    } else {
                        if (task.getException() != null)
                            onFailure.onFailure(task.getException());
                        else {
                            onFailure.onFailure(new Exception("Failed to sign in with google for unknown reason"));
                        }
                    }
                });
    }

    public String getUUID() {
        return mAuth.getCurrentUser().getUid();
    }
}
