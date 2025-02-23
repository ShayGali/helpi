package com.sibi.helpi.utils;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.repositories.UserRepository;
import java.util.List;

public class AdminNotificationHelper {
    private static final String TAG = "AdminNotificationHelper";
    private static AdminNotificationHelper instance;
    private final UserRepository userRepository;

    private AdminNotificationHelper() {
        this.userRepository = new UserRepository();
    }

    public static synchronized AdminNotificationHelper getInstance() {
        if (instance == null) {
            instance = new AdminNotificationHelper();
        }
        return instance;
    }

    public void sendNotificationForNewPost(Postable post) {
        // Get all admin users
        userRepository.getAllAdmins()
                .addOnSuccessListener(admins -> {
                    for (User admin : admins) {
                        if (admin.getFcmToken() != null) {
                            sendNotification(
                                    admin.getFcmToken(),
                                    post.getTitle(),
                                    post.getId()
                            );
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting admin users", e));
    }

    private void sendNotification(String adminToken, String postTitle, String postId) {
        Map<String, String> data = new HashMap<>();
        data.put("postId", postId);
        data.put("type", "new_post_review");
        data.put("title", "New Post for Review");
        data.put("message", "New post: " + postTitle);

        RemoteMessage.Builder builder = new RemoteMessage.Builder(adminToken)
                .setData(data);

        FirebaseMessaging.getInstance().send(builder.build());
    }
}