package com.sibi.helpi.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.navigation.NavDeepLinkBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sibi.helpi.R;
import com.sibi.helpi.repositories.UserRepository;

public class ChatFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ChatFirebaseMessaging";
    private static final String CHANNEL_ID = "chat_messages";
    private static final String CHANNEL_NAME = "Chat Messages";
    private static final String CHANNEL_DESCRIPTION = "Notifications for new chat messages";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String chatId = remoteMessage.getData().get("chatId");
            String message = remoteMessage.getData().get("message");
            String senderName = remoteMessage.getData().get("senderName");

            // Create and show notification
            sendNotification(chatId, senderName, message);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // Update token in Firebase Database
        UserRepository userRepository = new UserRepository();
        String userId = userRepository.getUUID();
        if (userId != null) {
            updateUserToken(token);
        }
    }

    private void updateUserToken(String token) {
        UserRepository userRepository = new UserRepository();
        String userId = userRepository.getUUID();
        if (userId != null) {
            userRepository.updateFcmToken(userId, token)
                    .addOnFailureListener(e -> Log.e(TAG, "Error updating user token", e));
        }
    }

    private void sendNotification(String chatId, String senderName, String messageBody) {
        createNotificationChannel();

        // Create pending intent for when user clicks notification
        PendingIntent pendingIntent = new NavDeepLinkBuilder(this)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.chatMessagesFragment)
                .setArguments(createBundleWithChatId(chatId))
                .createPendingIntent();

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat_24)
                        .setContentTitle(senderName)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Show the notification
        int notificationId = chatId.hashCode();
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    private Bundle createBundleWithChatId(String chatId) {
        Bundle bundle = new Bundle();
        bundle.putString("chatId", chatId);
        return bundle;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Chat Messages",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for new chat messages");
            channel.enableLights(true);
            channel.enableVibration(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void createNotificationChannel(Context context) {
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "Creating notification channel");
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            // Configure channel settings
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);

            // Register the channel with the system
            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                Log.d(TAG, "Registering notification channel");
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}