package com.sibi.helpi.models;

import java.util.List;

/**
 * Represents a chat between two users.
 */
public class Chat {
    private String chatId;
    private List<String> participants;
    private String lastMessage;
    private long timestamp;
    private String chatPartnerName;
    private String profileImageUrl; // Add this for partner's profile image
    private int unreadCount;        // Add this for unread messages count

    public Chat() {}

    public Chat(String chatId, List<String> participants, String lastMessage,
                long timestamp, String chatPartnerName, String profileImageUrl, int unreadCount) {
        this.chatId = chatId;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.chatPartnerName = chatPartnerName;
        this.profileImageUrl = profileImageUrl;
        this.unreadCount = unreadCount;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getChatPartnerName() {
        return chatPartnerName;
    }

    public void setChatPartnerName(String chatPartnerName) {
        this.chatPartnerName = chatPartnerName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    // Add a helper method to format timestamp
    public String getFormattedTimestamp() {
        // You can implement your own time formatting logic here
        // For example: "Today", "Yesterday", or "MM/dd/yyyy"
        return String.valueOf(timestamp);
    }
}
