package com.sibi.helpi.repositories;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.*;
import com.sibi.helpi.models.Chat;
import com.sibi.helpi.models.Message;
import com.sibi.helpi.models.User;
import com.sibi.helpi.utils.NotificationHelper;

import java.util.*;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private static ChatRepository instance;
    private final DatabaseReference db;
    private final MutableLiveData<Boolean> hasUnreadMessagesLiveData = new MutableLiveData<>(false);
    private static final String CHATS_REF = "chats";
    private static final String MESSAGES_REF = "messages";

    private ChatRepository() {
        String databaseUrl = "https://helpi-90503-default-rtdb.europe-west1.firebasedatabase.app";
        FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
        this.db = database.getReference();
    }

    public static synchronized ChatRepository getInstance() {
        if (instance == null) {
            instance = new ChatRepository();
        }
        return instance;
    }

    // Get all chats for current user
    public LiveData<List<Chat>> getUserChats(String currentUserId) {
        MutableLiveData<List<Chat>> chatsLiveData = new MutableLiveData<>();

        db.child(CHATS_REF)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Chat> chatsList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat != null && chat.getParticipants().contains(currentUserId)) {
                                chat.setChatId(snapshot.getKey());
                                chatsList.add(chat);
                            }
                        }
                        // Sort by timestamp in descending order
                        chatsList.sort((c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                        chatsLiveData.setValue(chatsList);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error getting chats", error.toException());
                    }
                });

        return chatsLiveData;
    }

    // Get messages for a specific chat
    public LiveData<List<Message>> getChatMessages(String chatId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        db.child(CHATS_REF)
                .child(chatId)
                .child(MESSAGES_REF)
                .orderByChild("timestamp")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Message> messagesList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            if (message != null) {
                                message.setMessageId(snapshot.getKey());
                                messagesList.add(message);
                            }
                        }
                        messagesLiveData.setValue(messagesList);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error getting messages", error.toException());
                    }
                });

        return messagesLiveData;
    }

    // Send a new message
    public void sendMessage(String currentUserId, String chatId, String messageText) {
        Log.d(TAG, "Sending message: " + messageText);
        db.child(CHATS_REF)
                .child(chatId)
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat != null) {
                        String receiverId = chat.getParticipants().stream()
                                .filter(id -> !id.equals(currentUserId))
                                .findFirst()
                                .orElse(null);

                        Message message = new Message();
                        message.setMessage(messageText);
                        message.setSenderId(currentUserId);
                        message.setReceiverId(receiverId);
                        message.setTimestamp(System.currentTimeMillis());
                        message.setSeen(false);

                        // Add message
                        DatabaseReference newMessageRef = db.child(CHATS_REF)
                                .child(chatId)
                                .child(MESSAGES_REF)
                                .push();

                        newMessageRef.setValue(message)
                                .addOnSuccessListener(aVoid -> {
                                    // Update last message and timestamp
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("lastMessage", messageText);
                                    updates.put("timestamp", message.getTimestamp());

                                    db.child(CHATS_REF)
                                            .child(chatId)
                                            .updateChildren(updates);

                                    // Increment unread count
                                    incrementUnreadCountForReceiver(chatId, currentUserId, receiverId);

                                    // Send notification
                                    NotificationHelper.getInstance()
                                            .sendNotificationForNewMessage(message, chatId);
                                });
                    }
                });
    }

    // Create a new chat
//    public void createNewChat(String currentUserId, String otherUserId, String otherUserName) {
//        Log.d(TAG, "Creating new chat between: " + currentUserId + " and " + otherUserId);
//
//        List<String> participants = Arrays.asList(currentUserId, otherUserId);
//        Map<String, Integer> unreadCounts = new HashMap<>();
//        unreadCounts.put(currentUserId, 0);
//        unreadCounts.put(otherUserId, 0);
//
//        Chat newChat = new Chat();
//        newChat.setParticipants(participants);
//        newChat.setTimestamp(System.currentTimeMillis());
//        newChat.setLastMessage("");
//        newChat.setUnreadCounts(unreadCounts);
//        newChat.setChatPartnerName(otherUserName);  // Set this immediately
//
//        DatabaseReference newChatRef = db.child(CHATS_REF).push();
//        String chatId = newChatRef.getKey();
//        newChat.setChatId(chatId);  // Set the ID
//
//        newChatRef.setValue(newChat)
//                .addOnSuccessListener(aVoid -> {
//                    Log.d(TAG, "Successfully created chat with ID: " + chatId);
//                    updateChatPartnerInfo(chatId, currentUserId, otherUserId);
//                })
//                .addOnFailureListener(e ->
//                        Log.e(TAG, "Failed to create chat", e));
//    }

    // Mark messages as read
    public void markMessagesAsRead(String chatId, String userId) {
        db.child(CHATS_REF)
                .child(chatId)
                .child("unreadCounts")
                .child(userId)
                .setValue(0)
                .addOnSuccessListener(aVoid ->
                        Log.d("ChatRepository", "Successfully marked messages as read"))
                .addOnFailureListener(e ->
                        Log.e("ChatRepository", "Failed to mark messages as read", e));
    }

    // Get single chat by ID
    public LiveData<Chat> getChatById(String chatId) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        db.child(CHATS_REF)
                .child(chatId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat != null) {
                            chat.setChatId(snapshot.getKey());
                            chatLiveData.setValue(chat);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error getting chat", error.toException());
                    }
                });

        return chatLiveData;
    }

    public LiveData<Chat> getChatByParticipants(String userId1, String userId2) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();
        Log.d("ChatRepository", "Searching for chat between users: " + userId1 + " and " + userId2);

        // Listen to all chats
        db.child(CHATS_REF)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Chat existingChat = null;

                        // Check all chats for these participants
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Chat chat = snapshot.getValue(Chat.class);
                            if (chat != null && chat.getParticipants() != null &&
                                    chat.getParticipants().contains(userId1) &&
                                    chat.getParticipants().contains(userId2)) {
                                chat.setChatId(snapshot.getKey());
                                existingChat = chat;
                                break;
                            }
                        }

                        if (existingChat != null) {
                            Log.d("ChatRepository", "Found existing chat with ID: " + existingChat.getChatId());
                            chatLiveData.setValue(existingChat);
                        } else {
                            Log.d("ChatRepository", "No existing chat found, creating new one");

                            // Create new chat
                            List<String> participants = Arrays.asList(userId1, userId2);
                            Map<String, Integer> unreadCounts = new HashMap<>();
                            unreadCounts.put(userId1, 0);
                            unreadCounts.put(userId2, 0);

                            Chat newChat = new Chat();
                            newChat.setParticipants(participants);
                            newChat.setTimestamp(System.currentTimeMillis());
                            newChat.setLastMessage("");
                            newChat.setUnreadCounts(unreadCounts);

                            DatabaseReference newChatRef = db.child(CHATS_REF).push();
                            String chatId = newChatRef.getKey();
                            newChat.setChatId(chatId);

                            newChatRef.setValue(newChat)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("ChatRepository", "Successfully created new chat with ID: " + chatId);
                                        // Important: Update LiveData with the new chat
                                        chatLiveData.setValue(newChat);
                                        // Update additional info
                                        updateChatPartnerInfo(chatId, userId1, userId2);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("ChatRepository", "Failed to create chat", e);
                                        chatLiveData.setValue(null);
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error getting chats", error.toException());
                        chatLiveData.setValue(null);
                    }
                });

        return chatLiveData;
    }

    private Chat findChatInSnapshot(DataSnapshot dataSnapshot, String userId1, String userId2) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Chat chat = snapshot.getValue(Chat.class);
            if (chat != null) {
                List<String> participants = chat.getParticipants();
                if (participants != null &&
                        participants.contains(userId1) &&
                        participants.contains(userId2)) {
                    chat.setChatId(snapshot.getKey());
                    return chat;
                }
            }
        }
        return null;
    }

    private void incrementUnreadCountForReceiver(String chatId, String senderId, String receiverId) {
        DatabaseReference unreadCountRef = db.child(CHATS_REF)
                .child(chatId)
                .child("unreadCounts")
                .child(receiverId);

        unreadCountRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError error, boolean committed, DataSnapshot currentData) {
                if (error != null) {
                    Log.e("ChatRepository", "Error incrementing unread count", error.toException());
                }
            }
        });
    }

    private void updateChatPartnerInfo(String chatId, String userId1, String userId2) {
        UserRepository userRepo = new UserRepository();

        // Get both users' information
        Task<User> user1Task = userRepo.getUserById(userId1);
        Task<User> user2Task = userRepo.getUserById(userId2);

        Tasks.whenAllSuccess(user1Task, user2Task)
                .addOnSuccessListener(users -> {
                    User user1 = (User) users.get(0);
                    User user2 = (User) users.get(1);

                    if (user1 != null && user2 != null) {
                        Map<String, Object> updates = new HashMap<>();
                        // For user1, show user2's name and vice versa
                        updates.put("partnerNames/" + userId1, user2.getFullName());
                        updates.put("partnerNames/" + userId2, user1.getFullName());
                        updates.put("profileImageUrl", user2.getProfileImgUri());

                        db.child(CHATS_REF)
                                .child(chatId)
                                .updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("ChatRepository", "Successfully updated chat partner info"))
                                .addOnFailureListener(e ->
                                        Log.e("ChatRepository", "Failed to update chat partner info", e));
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("ChatRepository", "Failed to get user information", e));
    }

    public LiveData<Boolean> observeUnreadMessages(String userId) {
        db.child(CHATS_REF)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean hasUnread = false;
                        for (DataSnapshot chatSnapshot : dataSnapshot.getChildren()) {
                            Chat chat = chatSnapshot.getValue(Chat.class);
                            if (chat != null && chat.getParticipants().contains(userId)) {
                                Map<String, Integer> unreadCounts = chat.getUnreadCounts();
                                if (unreadCounts != null && unreadCounts.containsKey(userId)) {
                                    Integer count = unreadCounts.get(userId);
                                    if (count != null && count > 0) {
                                        hasUnread = true;
                                        break;
                                    }
                                }
                            }
                        }
                        hasUnreadMessagesLiveData.setValue(hasUnread);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error observing unread messages", error.toException());
                    }
                });

        return hasUnreadMessagesLiveData;
    }
}