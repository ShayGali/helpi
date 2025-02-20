package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.CHATS_COLLECTION;
import static com.sibi.helpi.utils.AppConstants.MESSAGES_COLLECTION;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sibi.helpi.models.Chat;
import com.sibi.helpi.models.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatRepository {
    private static ChatRepository instance;
    private final FirebaseFirestore db;
    private final MutableLiveData<Boolean> hasUnreadMessagesLiveData = new MutableLiveData<>(false);

    private ChatRepository() {
        this.db = FirebaseFirestore.getInstance();
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

        db.collection(CHATS_COLLECTION)
                .whereArrayContains("participants", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<Chat> chatsList = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Chat chat = doc.toObject(Chat.class);
                            chat.setChatId(doc.getId());
                            chatsList.add(chat);
                        }
                    }
                    chatsLiveData.setValue(chatsList);
                });

        return chatsLiveData;
    }

    // Get messages for a specific chat
    public LiveData<List<Message>> getChatMessages(String chatId) {
        MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();

        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .collection(MESSAGES_COLLECTION)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    List<Message> messagesList = new ArrayList<>();
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Message message = doc.toObject(Message.class);
                            message.setMessageId(doc.getId());
                            messagesList.add(message);
                        }
                    }
                    messagesLiveData.setValue(messagesList);
                });

        return messagesLiveData;
    }

    // Send a new message
    public void sendMessage(String currentUserId, String chatId, String messageText) {
        // First, get the chat document directly from Firestore
        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if (chat != null) {
                        // Get receiver ID
                        String receiverId = chat.getParticipants().stream()
                                .filter(id -> !id.equals(currentUserId))
                                .findFirst()
                                .orElse(null);

                        // Create and send message
                        Message message = new Message();
                        message.setMessage(messageText);
                        message.setSenderId(currentUserId);
                        message.setReceiverId(receiverId);
                        message.setTimestamp(System.currentTimeMillis());
                        message.setSeen(false);

                        // Add message and update unread count
                        db.collection(CHATS_COLLECTION)
                                .document(chatId)
                                .collection(MESSAGES_COLLECTION)
                                .add(message)
                                .addOnSuccessListener(ref -> {
                                    updateChatLastMessage(chatId, messageText);
                                    incrementUnreadCountForReceiver(chatId, currentUserId, receiverId);
                                });
                    }
                });
    }

    // Create a new chat
    public void createNewChat(String currentUserId, String otherUserId, String otherUserName) {
        List<String> participants = new ArrayList<>();
        participants.add(currentUserId);
        participants.add(otherUserId);

        // Initialize with empty unread counts map for the new structure
        Map<String, Integer> unreadCounts = new HashMap<>();
        unreadCounts.put(currentUserId, 0);
        unreadCounts.put(otherUserId, 0);

        Chat newChat = new Chat();
        newChat.setParticipants(participants);
        newChat.setTimestamp(System.currentTimeMillis());
        newChat.setLastMessage("");
        newChat.setUnreadCounts(unreadCounts);  // Using new unread counts structure

        db.collection(CHATS_COLLECTION)
                .add(newChat)
                .addOnSuccessListener(documentReference -> {
                    String chatId = documentReference.getId();
                    updateChatPartnerInfo(chatId, currentUserId, otherUserId);
                });
    }

    // Update last message in chat
    private void updateChatLastMessage(String chatId, String lastMessage) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("lastMessage", lastMessage);
        updates.put("timestamp", System.currentTimeMillis());

        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .update(updates);
    }

    // Mark messages as read
    public void markMessagesAsRead(String chatId, String userId) {
        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> unreadCounts = new HashMap<>();
                    if (documentSnapshot.contains("unreadCounts")) {
                        unreadCounts = (Map<String, Object>) documentSnapshot.get("unreadCounts");
                    }
                    // Set unread count to 0 for current user
                    unreadCounts.put(userId, 0L);

                    // Update Firestore
                    db.collection(CHATS_COLLECTION)
                            .document(chatId)
                            .update("unreadCounts", unreadCounts)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("ChatRepository", "Successfully marked messages as read");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("ChatRepository", "Failed to mark messages as read", e);
                            });
                });
    }

    // Get single chat by ID
    public LiveData<Chat> getChatById(String chatId) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null || documentSnapshot == null) {
                        return;
                    }

                    Chat chat = documentSnapshot.toObject(Chat.class);
                    if (chat != null) {
                        chat.setChatId(documentSnapshot.getId());
                        chatLiveData.setValue(chat);
                    }
                });

        return chatLiveData;
    }

    public LiveData<Chat> getChatByParticipants(String userId1, String userId2) {
        MutableLiveData<Chat> chatLiveData = new MutableLiveData<>();

        db.collection(CHATS_COLLECTION)
                .whereArrayContainsAny("participants", Arrays.asList(userId1, userId2))
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Chat existingChat = null;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        Chat chat = doc.toObject(Chat.class);
                        if (chat != null) {
                            List<String> participants = chat.getParticipants();
                            if (participants.contains(userId1) && participants.contains(userId2)) {
                                chat.setChatId(doc.getId());
                                existingChat = chat;
                                // TODO: complete this
//                                updateChatPartnerInfo(chat.getChatId(), userId1, userId2);
                                break;
                            }
                        }
                    }

                    if (existingChat != null) {
                        chatLiveData.setValue(existingChat);
                    } else {
                        // Create new chat if none exists
                        createNewChat(userId1, userId2, "");  // This will trigger the observer
                    }
                });

        return chatLiveData;
    }

    private void incrementUnreadCountForReceiver(String chatId, String senderId, String receiverId) {
        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // Get existing unread counts or create new map
                    Map<String, Object> unreadCounts = new HashMap<>();
                    if (documentSnapshot.contains("unreadCounts")) {
                        unreadCounts = (Map<String, Object>) documentSnapshot.get("unreadCounts");
                    }

                    // Get current count as Long
                    Object currentCountObj = unreadCounts.getOrDefault(receiverId, 0L);
                    long currentCount = 0;
                    if (currentCountObj instanceof Long) {
                        currentCount = (Long) currentCountObj;
                    } else if (currentCountObj instanceof Integer) {
                        currentCount = ((Integer) currentCountObj).longValue();
                    }

                    // Increment unread count for receiver ONLY
                    unreadCounts.put(receiverId, currentCount + 1);
                    // Make sure sender's count stays at 0
                    unreadCounts.put(senderId, 0L);

                    db.collection(CHATS_COLLECTION)
                            .document(chatId)
                            .update("unreadCounts", unreadCounts);
                });
    }
//    private void updateChatPartnerInfo(String chatId, String userId, String partnerId) {
//        UserRepository userRepo = new UserRepository();
//        userRepo.getUser(partnerId).addOnSuccessListener(user -> {
//            if (user != null) {
//                Map<String, Object> updates = new HashMap<>();
//                updates.put("chatPartnerName", user.getDisplayName());
//                updates.put("profileImageUrl", user.getProfileImageUrl());
//
//                db.collection(CHATS_COLLECTION)
//                        .document(chatId)
//                        .update(updates);
//            }
//        });
//    }
    private void updateChatPartnerInfo(String chatId, String userId, String partnerId) {
        UserRepository userRepo = new UserRepository();
        userRepo.getUserById(partnerId)
                .addOnSuccessListener(user -> {
                    if (user != null) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("chatPartnerName", user.getFullName());
                        updates.put("profileImageUrl", user.getProfileImgUri());

                        db.collection(CHATS_COLLECTION)
                                .document(chatId)
                                .update(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("ChatRepository", "Successfully updated chat partner info"))
                                .addOnFailureListener(e ->
                                        Log.e("ChatRepository", "Failed to update chat partner info", e));
                    }
                })
                .addOnFailureListener(e ->
                        Log.e("ChatRepository", "Failed to get user info", e));
    }

    public LiveData<Boolean> observeUnreadMessages(String userId) {
        db.collection(CHATS_COLLECTION)
                .whereArrayContains("participants", userId)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }

                    boolean hasUnread = false;
                    if (value != null) {
                        for (QueryDocumentSnapshot doc : value) {
                            Map<String, Object> unreadCounts = (Map<String, Object>) doc.get("unreadCounts");
                            if (unreadCounts != null) {
                                Object userCount = unreadCounts.get(userId);
                                if (userCount instanceof Long && (Long) userCount > 0) {
                                    hasUnread = true;
                                    break;
                                }
                            }
                        }
                    }
                    hasUnreadMessagesLiveData.setValue(hasUnread);
                });

        return hasUnreadMessagesLiveData;
    }
}