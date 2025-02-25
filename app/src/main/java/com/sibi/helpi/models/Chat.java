package com.sibi.helpi.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents a chat between two users.
 */
public class Chat {
    private String chatId;
    private List<String> participants;
    private String lastMessage = "";
    private long timestamp;
    private Map<String, String> partnerNames;
    private String profileImageUrl = "";
    private int unreadCount;
    private Map<String, Integer> unreadCounts;

    public Chat() {
        this.participants = new ArrayList<>();
        this.partnerNames = new HashMap<>();
        this.unreadCounts = new HashMap<>();
    }

    public Chat(String chatId, List<String> participants, String lastMessage,
                long timestamp, String profileImageUrl, int unreadCount) {
        this.chatId = chatId;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.profileImageUrl = profileImageUrl;
        this.unreadCount = unreadCount;
        partnerNames = new HashMap<>();
        unreadCounts = new HashMap<>();
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

    public Map<String, String> getPartnerNames() {
        return partnerNames != null ? partnerNames : new HashMap<>();
    }

    public void setPartnerNames(Map<String, String> partnerNames) {
        this.partnerNames = partnerNames != null ? partnerNames : new HashMap<>();
    }

    public String getChatPartnerName(String currentUserId) {
        if (partnerNames != null && currentUserId != null) {
            return partnerNames.getOrDefault(currentUserId, "Chat Partner");
        }
        return "Chat Partner";
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getFormattedTimestamp() {
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - timestamp;

        Calendar currentCalendar = Calendar.getInstance();
        Calendar timestampCalendar = Calendar.getInstance();
        timestampCalendar.setTimeInMillis(timestamp);

        if (timeDifference < TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)) {
            // Format as time
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFormat.format(new Date(timestamp));
        } else if (currentCalendar.get(Calendar.YEAR) == timestampCalendar.get(Calendar.YEAR)) {
            // Format as date without year
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return dateFormat.format(new Date(timestamp));
        } else {
            // Format as date with year
            SimpleDateFormat dateFormatWithYear = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormatWithYear.format(new Date(timestamp));
        }
    }

    public Map<String, Integer> getUnreadCounts() {
        return unreadCounts;
    }

    public void setUnreadCounts(Map<String, Integer> unreadCounts) {
        this.unreadCounts = unreadCounts;
    }

    public int getUnreadCount(String currentUserId) {
        return unreadCounts.getOrDefault(currentUserId, 0);
    }
}
