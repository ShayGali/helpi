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
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.repositories.UserRepository;

public class GenFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "ChatFirebaseMessaging";
    private static final String CHANNEL_ID = "chat_messages";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            String type = remoteMessage.getData().get("type");

            if ("new_message".equals(type)) {
                handleChatMessage(remoteMessage);
            } else if ("new_post_review".equals(type)) {
                handleNewPostNotification(remoteMessage);
            }
        }
    }

    private void handleChatMessage(@NonNull RemoteMessage remoteMessage){
        String chatId = remoteMessage.getData().get("chatId");
        String senderName = remoteMessage.getData().get("senderName");
        String messageBody = remoteMessage.getData().get("messageBody");

        sendNotification(chatId, senderName, messageBody);
    }

    private void handleNewPostNotification(RemoteMessage remoteMessage) {
        String postId = remoteMessage.getData().get("postId");
        String title = remoteMessage.getData().get("title");
        String message = remoteMessage.getData().get("message");

        // Create intent for when notification is clicked
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add extra data for navigation
        Bundle args = new Bundle();
        args.putString("postId", postId);
        args.putString("sourcePage", "AdminDashBoardFragment");
        args.putBoolean("fromNotification", true);
        intent.putExtras(args);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                postId.hashCode(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_chat_24)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(postId.hashCode(), notificationBuilder.build());
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
}