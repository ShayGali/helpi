package com.sibi.helpi.utils;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.sibi.helpi.models.Message;
import com.sibi.helpi.models.User;
import com.sibi.helpi.repositories.UserRepository;
import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    private static final String TAG = "NotificationHelper";
    private static NotificationHelper instance;
    private final UserRepository userRepository;

    private NotificationHelper() {
        this.userRepository = new UserRepository();
    }

    public static synchronized NotificationHelper getInstance() {
        if (instance == null) {
            instance = new NotificationHelper();
        }
        return instance;
    }

    public void sendNotificationForNewMessage(Message message, String chatId) {
        // Don't send notification if sender is recipient
        if (message.getSenderId().equals(message.getReceiverId())) {
            return;
        }

        // Get the recipient's FCM token
        userRepository.getUserById(message.getReceiverId())
                .addOnSuccessListener(recipient -> {
                    if (recipient != null && recipient.getFcmToken() != null) {
                        // Get sender's name
                        userRepository.getUserById(message.getSenderId())
                                .addOnSuccessListener(sender -> {
                                    if (sender != null) {
                                        sendNotification(
                                                recipient.getFcmToken(),
                                                sender.getFullName(),
                                                message.getMessage(),
                                                chatId
                                        );
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error getting sender details", e));
                    }
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Error getting recipient details", e));
    }

    private void sendNotification(String recipientToken, String senderName,
                                  String messageContent, String chatId) {
        // Create notification data
        Map<String, String> data = new HashMap<>();
        data.put("chatId", chatId);
        data.put("message", messageContent);
        data.put("senderName", senderName);
        data.put("type", "new_message");

        // Create FCM message
        RemoteMessage.Builder builder = new RemoteMessage.Builder(recipientToken)
                .setData(data);

        // Send the message
        FirebaseMessaging.getInstance().send(builder.build());
    }
}
