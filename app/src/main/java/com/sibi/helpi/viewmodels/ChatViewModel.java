package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sibi.helpi.models.Chat;
import com.sibi.helpi.models.Message;
import com.sibi.helpi.repositories.ChatRepository;
import java.util.List;

public class ChatViewModel extends ViewModel {
    private final ChatRepository chatRepository;
    private LiveData<List<Chat>> chatsListLiveData;
    private LiveData<List<Message>> messagesLiveData;

    public ChatViewModel() {
        chatRepository = new ChatRepository();
    }

    public LiveData<List<Chat>> getChatsList(String currentUserId) {
        if (chatsListLiveData == null) {
            chatsListLiveData = chatRepository.getUserChats(currentUserId);
        }
        return chatsListLiveData;
    }
    public LiveData<List<Message>> getChatMessages(String chatId) {
        messagesLiveData = chatRepository.getChatMessages(chatId);
        return messagesLiveData;
    }

    public void sendMessage(String currentUSerId, String chatId, String messageText) {
        chatRepository.sendMessage(currentUSerId, chatId, messageText);
    }

    public void createNewChat(String currentUserId, String otherUserId, String otherUserName) {
        chatRepository.createNewChat(currentUserId, otherUserId, otherUserName);
    }

    public void markChatAsRead(String chatId) {
        chatRepository.markChatAsRead(chatId);
    }

    public LiveData<Chat> getChatById(String chatId) {
        return chatRepository.getChatById(chatId);
    }

    public LiveData<Chat> getChatByParticipants(String userId1, String userId2) {
        return chatRepository.getChatByParticipants(userId1, userId2);
    }
}