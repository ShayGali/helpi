package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.CHATS_COLLECTION;
import static com.sibi.helpi.utils.AppConstants.MESSAGES_COLLECTION;

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
    private final FirebaseFirestore db;
    public ChatRepository() {
        this.db = FirebaseFirestore.getInstance();
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
                                    incrementUnreadCount(chatId, receiverId);
                                });
                    }
                });
    }

    // Create a new chat
    public void createNewChat(String currentUserId, String otherUserId, String otherUserName) {
        // TODO: Check if chat already exists
        List<String> participants = new ArrayList<>();
        participants.add(currentUserId);
        participants.add(otherUserId);

        Chat newChat = new Chat();
        newChat.setParticipants(participants);
        newChat.setTimestamp(System.currentTimeMillis());
        newChat.setChatPartnerName(otherUserName);
        newChat.setLastMessage("");
        newChat.setUnreadCount(0);

        db.collection(CHATS_COLLECTION)
                .add(newChat);

//        // TODO: use this instead after fixing the function below
//        db.collection(CHATS_COLLECTION)
//                .add(newChat)
//                .addOnSuccessListener(documentReference -> {
//                    String chatId = documentReference.getId();
//                    // Update chat partner info after chat creation
//                    updateChatPartnerInfo(chatId, currentUserId, otherUserId);
//                });
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
    public void markChatAsRead(String chatId) {
        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .update("unreadCount", 0);
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

    private void incrementUnreadCount(String chatId, String receiverId) {
        db.collection(CHATS_COLLECTION)
                .document(chatId)
                .update("unreadCount", com.google.firebase.firestore.FieldValue.increment(1));
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
}