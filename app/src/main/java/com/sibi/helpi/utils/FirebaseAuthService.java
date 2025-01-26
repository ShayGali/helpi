package com.sibi.helpi.utils;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sibi.helpi.models.User;

import java.util.Objects;

public class FirebaseAuthService{
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final CollectionReference userCollection;

    public FirebaseAuthService() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userCollection = db.collection("users");
    }

    public Task<DocumentReference> registerUser(User user, String password) {
        return auth.createUserWithEmailAndPassword(user.getEmail(), password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Save user data after successful authentication
                    String uid = getCurrentUserId();
                    DocumentReference userDoc = userCollection.document(uid);
                    return userDoc.set(user).continueWith(t -> userDoc);
                });
    }

    public Task<DocumentReference> signInWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        return auth.signInWithCredential(credential)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    boolean isNewUser = task.getResult().getAdditionalUserInfo().isNewUser();
                    if (isNewUser) {
                        // Create new user document for Google sign-in
                        User user = new User(
                                account.getEmail(),
                                account.getGivenName(),
                                account.getFamilyName(),
                                "",
                                account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : ""
                        );
                        DocumentReference userDoc = userCollection.document(getCurrentUserId());
                        return userDoc.set(user).continueWith(t -> userDoc);
                    }
                    // Return existing user document reference
                    return Tasks.forResult(userCollection.document(getCurrentUserId()));
                });
    }

    public String getCurrentUserId() {
        if (auth.getCurrentUser() == null) {
            throw new IllegalStateException("No user is currently signed in");
        }
        return auth.getCurrentUser().getUid();
    }

    public Task<Void> signOut() {
        auth.signOut();
        return Tasks.forResult(null);
    }

    public boolean isUserSignedIn() {
        return auth.getCurrentUser() != null;
    }
}
