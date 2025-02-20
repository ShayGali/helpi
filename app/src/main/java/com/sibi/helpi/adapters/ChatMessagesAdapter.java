package com.sibi.helpi.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.sibi.helpi.R;
import com.sibi.helpi.models.Message;
import java.util.List;

public class ChatMessagesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_PARTNER = 2;
    private List<Message> messages;

    public ChatMessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateMessages(List<Message> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_PARTNER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_USER) {
            View view = inflater.inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_partner, parent, false);
            return new PartnerMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else {
            ((PartnerMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;

        UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_body);
        }

        void bind(Message message) {
            messageTextView.setText(message.getMessage());
        }
    }

    static class PartnerMessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageTextView;
        private final ImageView profileImageView;

        PartnerMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.text_message_body);
            profileImageView = itemView.findViewById(R.id.image_profile);
        }

        void bind(Message message) {
            messageTextView.setText(message.getMessage());
            // Set profile image using Glide if available
            // Glide.with(itemView.getContext())
            //     .load(message.getProfileImageUrl())
            //     .into(profileImageView);
        }
    }
}