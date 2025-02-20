package com.sibi.helpi.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sibi.helpi.R;
import com.sibi.helpi.models.Chat;

import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {
    private List<Chat> chatList;
    private final OnChatClickListener onChatClickListener;

    public interface OnChatClickListener {
        void onChatClick(Chat chat);
    }

    public ChatListAdapter(OnChatClickListener onChatClickListener) {
        this.chatList = new ArrayList<>();
        this.onChatClickListener = onChatClickListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        holder.bind(chat);
        holder.itemView.setOnClickListener(v -> onChatClickListener.onChatClick(chat));
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView profileImageView;
        private final TextView chatPartnerNameTextView;
        private final TextView lastMessageTextView;
        private final TextView timestampTextView;
        private final TextView unreadCountTextView;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profile_image);
            chatPartnerNameTextView = itemView.findViewById(R.id.chat_partner_name);
            lastMessageTextView = itemView.findViewById(R.id.last_message);
            timestampTextView = itemView.findViewById(R.id.timestamp);
            unreadCountTextView = itemView.findViewById(R.id.unread_count);
        }

        void bind(Chat chat) {
            // Set chat partner name
            chatPartnerNameTextView.setText(chat.getChatPartnerName());

            // Set last message
            lastMessageTextView.setText(chat.getLastMessage());

            // Set timestamp
            timestampTextView.setText(chat.getFormattedTimestamp());

            // Handle unread count
            if (chat.getUnreadCount() > 0) {
                unreadCountTextView.setVisibility(View.VISIBLE);
                unreadCountTextView.setText(String.valueOf(chat.getUnreadCount()));
            } else {
                unreadCountTextView.setVisibility(View.GONE);
            }

            // Load profile image if available
            String profileImageUrl = chat.getProfileImageUrl();
            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                profileImageView.setVisibility(View.VISIBLE);
                Glide.with(itemView.getContext())
                        .load(profileImageUrl)
                        .circleCrop()
                        .placeholder(R.drawable.ic_chat_24)
                        .into(profileImageView);
            } else {
                Glide.with(itemView.getContext())
                        .load(R.drawable.ic_chat_24)
                        .circleCrop()
                        .into(profileImageView);
            }
        }
    }
}