package com.sibi.helpi.repositories;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sibi.helpi.models.Pair;
import com.sibi.helpi.models.User;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.FirebaseAuthService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class UserRepository {

    private static final UserRepository instance = new UserRepository();

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


    public static UserRepository getInstance() {
        return instance;
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
        return authService.signInWithGoogle(account)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return updateUserFcmTokenOnSignIn()
                            .continueWith(tokenTask -> task.getResult());
                });
    }

    public Task<Void> signOut(GoogleSignInClient googleSignInClient) {
        String userId = getUUID();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // First remove the FCM token
        Task<Void> removeTokenTask = (userId != null)
                ? removeFcmToken(userId)
                : Tasks.forResult(null);

        return removeTokenTask
                .continueWithTask(task -> authService.signOut())
                .continueWithTask(task -> {
                    mAuth.signOut();
                    return googleSignInClient.signOut();
                });
    }

    public Task<Void> deleteAccount() {
        //TODO: delete all user posts

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
        if (authService.getCurrentUserId() == null) {
            return null; // Return null instead of throwing an exception
        }
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
                    // Update FCM token after successful sign in
                    return updateUserFcmTokenOnSignIn();
                })
                .continueWithTask(task -> getCurrentUser());
    }

    public Task<User> getUserById(String userId) {
        return userCollection.document(userId).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return task.getResult().toObject(User.class);
                });
    }

    public LiveData<User> getUserByIdLiveData(String userId) {
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        userCollection.document(userId).addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "getCurrentUserLiveData: Error", e);
                return;
            }
            if (documentSnapshot != null && documentSnapshot.exists()) {
                User user = documentSnapshot.toObject(User.class);
                userLiveData.postValue(user);
            }
        });
        return userLiveData;
    }


    private Task<List<Pair<User, String>>> getUsersByField(String field, String expectedValue) {
        return userCollection.whereEqualTo(field, expectedValue)
                .get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot == null || querySnapshot.isEmpty()) {
                        Log.d(TAG, "getUsersByField: No users found with " + field + " = " + expectedValue);
                        return Collections.emptyList(); // Return an empty list if no users found
                    }

                    List<Pair<User, String>> usersList = querySnapshot.getDocuments().stream()
                            .map(documentSnapshot -> {
                                User user = documentSnapshot.toObject(User.class);
                                return new Pair<>(user, documentSnapshot.getId());
                            })
                            .collect(Collectors.toList());

                    Log.d(TAG, "getUsersByField: Found " + usersList.size() + " users with " + field + " = " + expectedValue);
                    return usersList;
                });
    }





    private Task<Boolean> updateUserField(String userId, String field, Object value) {
        return userCollection.document(userId).update(field, value)
                .continueWith(Task::isSuccessful);
    }

    public Task<Boolean> addAdmin(String email, AppConstants.UserType userType) {
        return getUsersByField("email", email)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    List<Pair<User, String>> users = task.getResult();
                    if (users == null || users.isEmpty()) {
                        Log.d(TAG, "addAdmin: No user found with email: " + email);
                        return Tasks.forResult(false); // Return false if user not found
                    }

                    String UID = users.get(0).getSecond();
                    return updateUserField(UID, "userType", userType).continueWith(updateTask -> {
                        if (!updateTask.isSuccessful()) {
                            throw Objects.requireNonNull(updateTask.getException());
                        }
                        return updateTask.getResult();
                    });
                });
    }

    /**
     * Updates the FCM token for a specific user
     */
    public Task<Void> updateFcmToken(String userId, String token) {
        return userCollection.document(userId)
                .update("fcmToken", token)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM Token updated successfully"))
                .addOnFailureListener(e -> Log.e(TAG, "Error updating FCM token", e));
    }

    /**
     * Retrieves the FCM token for a specific user
     */
    public Task<String> getFcmToken(String userId) {
        return userCollection.document(userId).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    DocumentSnapshot document = task.getResult();
                    return document.getString("fcmToken");
                });
    }

    /**
     * Updates the user's FCM token during sign in
     */
    public Task<Void> updateUserFcmTokenOnSignIn() {
        return FirebaseMessaging.getInstance().getToken()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    String token = task.getResult();
                    String userId = getUUID();
                    if (userId != null && token != null) {
                        return updateFcmToken(userId, token);
                    }
                    return Tasks.forException(new Exception("User ID or token is null"));
                });
    }

    /**
     * Removes the FCM token when user signs out
     */
    public Task<Void> removeFcmToken(String userId) {
        return updateFcmToken(userId, "");
    }

   public Task<Boolean> updateProfile(String firstName, String lastName, String phoneNumber, String email, byte[] profileImg) {
    return getCurrentUser().continueWithTask(task -> {
        if (!task.isSuccessful()) {
            throw Objects.requireNonNull(task.getException());
        }
        User currentUser = task.getResult();
        if (currentUser == null) {
            return Tasks.forResult(false);
        }
        if (firstName != null && !firstName.isEmpty()) {
            currentUser.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            currentUser.setLastName(lastName);
        }
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            currentUser.setPhoneNumber(phoneNumber);
        }
        if (email != null && !email.isEmpty()) {
            currentUser.setEmail(email);
        }

        if (profileImg != null) {
            return uploadProfileImageAndSaveUser(currentUser, profileImg)
                    .continueWith(task1 -> {
                        if (!task1.isSuccessful()) {
                            throw Objects.requireNonNull(task1.getException());
                        }
                        return true;
                    });
        }
        else {
            return saveUserData(currentUser, getUUID())
                    .continueWith(task1 -> {
                        if (!task1.isSuccessful()) {
                            Log.e(TAG, "updateProfile: Failed to save user data", task1.getException());
                        }
                        return true;
                    });
        }
    });
}

public Task<Boolean> isEmailTaken(String email) {
    return getUsersByField("email", email)
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "isEmailTaken: Failed to check email", task.getException());
                }
                return !task.getResult().isEmpty();
            });
}

public Task<Boolean> getUserNotificationStatus(String userId) {
    return userCollection.document(userId).get()
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "getUserNotificationStatus: Failed to get user notification status", task.getException());
                    return false;
                }
                DocumentSnapshot document = task.getResult();
                return document.getBoolean("notificationEnabled");
            });
}

public Task<Boolean> updateUserNotificationStatus(String userId, boolean isEnabled) {
    return userCollection.document(userId).update("notificationEnabled", isEnabled)
            .continueWith(task -> {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "updateUserNotificationStatus: Failed to update notification status", task.getException());
                }
                return task.isSuccessful();
            });
}

}
