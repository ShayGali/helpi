package com.sibi.helpi.repositories;


import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sibi.helpi.models.User;
import com.sibi.helpi.utils.FirebaseAuthService;

import java.util.Objects;

public class UserRepository {
    private static final String TAG = "UserRepository";
    public static final String COLLECTION_NAME = "users";
    private final FirebaseAuth mAuth;
    private final FirebaseAuthService authService;
    private final ImagesRepository imagesRepository;
    private final CollectionReference userCollection;

    public UserRepository() {
        authService = new FirebaseAuthService();
        mAuth = FirebaseAuth.getInstance();
        imagesRepository = ImagesRepository.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        userCollection = db.collection(COLLECTION_NAME);
    }

    public Task<DocumentReference> registerUser(User user, String password, byte[] profileImg) {
        return authService.registerUser(user, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return uploadProfileImageAndSaveUser(user, profileImg);
                });
    }

    private Task<DocumentReference> uploadProfileImageAndSaveUser(User user, byte[] profileImg) {
        String uid = authService.getCurrentUserId();
        if (profileImg == null) {
            user.setProfileImgUri("");
            return Tasks.forResult(null);
        }

        return imagesRepository.uploadProfileImage(uid, profileImg)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        user.setProfileImgUri(task.getResult().toString());
                        return saveUserData(user, uid);
                    }
                    return Tasks.forResult(null);
                });
    }

    private Task<DocumentReference> saveUserData(User user, String uid) {
        DocumentReference documentReference = userCollection.document(uid);
        return documentReference.set(user)
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return documentReference;
                });
    }

    public Task<DocumentReference> authWithGoogle(GoogleSignInAccount account) {
        return authService.signInWithGoogle(account);
    }

    public Task<Void> signOut(GoogleSignInClient googleSignInClient) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        return authService.signOut()
                .continueWithTask(task -> googleSignInClient.signOut());
    }

    public Task<Void> deleteAccount() {
        // check if the user is signed in
        if (mAuth.getCurrentUser() == null) {
            Log.d(TAG, "deleteAccount: No user is currently signed in");
            return Tasks.forException(new Exception("No user is currently signed in"));
        }
        String uuid = getUUID();
        Task<Void> deleteProfileImageTask = imagesRepository.deleteProfileImage(uuid)
                .addOnFailureListener(e -> Log.e(TAG, "deleteAccount: Failed to delete profile image", e));

        Task<Void> deleteUserDataTask = userCollection.document(uuid).delete();

        Task<Void> deleteUserAccountTask = mAuth.getCurrentUser().delete();

        return Tasks.whenAll(deleteProfileImageTask, deleteUserDataTask, deleteUserAccountTask);
    }

    public String getUUID() {
        return authService.getCurrentUserId();
    }

    public Task<User> getCurrentUser() {
        String uuid = getUUID();
        if (uuid == null) {
            Log.d(TAG, "getCurrentUser: No UUID available");
            return Tasks.forException(new Exception("No user logged in"));
        }

        return userCollection.document(uuid).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "getCurrentUser: Failed to fetch user", task.getException());
                        throw Objects.requireNonNull(task.getException());
                    }

                    User user = task.getResult().toObject(User.class);
                    if (user == null) {
                        Log.w(TAG, "getCurrentUser: Document exists but user is null");
                        throw new Exception("User data not found");
                    }

                    Log.d(TAG, "getCurrentUser: Successfully fetched user data");
                    return user;
                });
    }

    public Task<User> signInWithEmail(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return getCurrentUser();
                });
    }


}
