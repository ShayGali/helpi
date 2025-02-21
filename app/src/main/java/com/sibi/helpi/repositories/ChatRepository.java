package com.sibi.helpi.repositories;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.firebase.database.*;
import com.sibi.helpi.models.Chat;
import com.sibi.helpi.models.Message;
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
                                });
                    }
                });
    }

    // Create a new chat
    public void createNewChat(String currentUserId, String otherUserId, String otherUserName) {
        Log.d(TAG, "Creating new chat between: " + currentUserId + " and " + otherUserId);

        List<String> participants = Arrays.asList(currentUserId, otherUserId);
        Map<String, Integer> unreadCounts = new HashMap<>();
        unreadCounts.put(currentUserId, 0);
        unreadCounts.put(otherUserId, 0);

        Chat newChat = new Chat();
        newChat.setParticipants(participants);
        newChat.setTimestamp(System.currentTimeMillis());
        newChat.setLastMessage("");
        newChat.setUnreadCounts(unreadCounts);
        newChat.setChatPartnerName(otherUserName);  // Set this immediately

        DatabaseReference newChatRef = db.child(CHATS_REF).push();
        String chatId = newChatRef.getKey();
        newChat.setChatId(chatId);  // Set the ID

        newChatRef.setValue(newChat)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Successfully created chat with ID: " + chatId);
                    updateChatPartnerInfo(chatId, currentUserId, otherUserId);
                })
                .addOnFailureListener(e ->
                        Log.e(TAG, "Failed to create chat", e));
    }

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

        // First try to find chats where userId1 is first participant
        db.child(CHATS_REF)
                .orderByChild("participants/0")
                .equalTo(userId1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d("ChatRepository", "Checking first query with " + dataSnapshot.getChildrenCount() + " results");

                        // Check in first query results
                        Chat existingChat = findChatInSnapshot(dataSnapshot, userId1, userId2);

                        if (existingChat != null) {
                            Log.d("ChatRepository", "Found chat in first query with ID: " + existingChat.getChatId());
                            chatLiveData.setValue(existingChat);
                        } else {
                            // If not found, try the second query
                            db.child(CHATS_REF)
                                    .orderByChild("participants/0")
                                    .equalTo(userId2)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Log.d("ChatRepository", "Checking second query with " + dataSnapshot.getChildrenCount() + " results");

                                            Chat existingChat = findChatInSnapshot(dataSnapshot, userId1, userId2);

                                            if (existingChat != null) {
                                                Log.d("ChatRepository", "Found chat in second query with ID: " + existingChat.getChatId());
                                                chatLiveData.setValue(existingChat);
                                            } else {
                                                Log.d("ChatRepository", "No existing chat found, creating new one");
                                                createNewChat(userId1, userId2, "");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError error) {
                                            Log.e("ChatRepository", "Error in second query: " + error.getMessage());
                                            chatLiveData.setValue(null);
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("ChatRepository", "Error in first query: " + error.getMessage());
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

    private void updateChatPartnerInfo(String chatId, String userId, String partnerId) {
        UserRepository userRepo = new UserRepository();
        userRepo.getUserById(partnerId)
                .addOnSuccessListener(user -> {
                    if (user != null) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("chatPartnerName", user.getFullName());
                        updates.put("profileImageUrl", user.getProfileImgUri());

                        db.child(CHATS_REF)
                                .child(chatId)
                                .updateChildren(updates)
                                .addOnSuccessListener(aVoid ->
                                        Log.d("ChatRepository", "Successfully updated chat partner info"))
                                .addOnFailureListener(e ->
                                        Log.e("ChatRepository", "Failed to update chat partner info", e));
                    }
                });
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