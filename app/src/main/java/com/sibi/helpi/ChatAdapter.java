// File: app/src/main/java/com/sibi/helpi/ChatAdapter.java

package com.sibi.helpi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_PARTNER = 2;
    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isUser()) {
            return VIEW_TYPE_USER;
        } else {
            return VIEW_TYPE_PARTNER;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_user, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_partner, parent, false);
            return new PartnerMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_USER) {
            ((UserMessageViewHolder) holder).bind(message);
        } else {
            ((PartnerMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageBody;

        UserMessageViewHolder(View itemView) {
            super(itemView);
            textMessageBody = itemView.findViewById(R.id.text_message_body);
        }

        void bind(Message message) {
            textMessageBody.setText(message.getText());
        }
    }

    static class PartnerMessageViewHolder extends RecyclerView.ViewHolder {
        TextView textMessageBody;
        ImageView imageProfile;

        PartnerMessageViewHolder(View itemView) {
            super(itemView);
            textMessageBody = itemView.findViewById(R.id.text_message_body);
            imageProfile = itemView.findViewById(R.id.image_profile);
        }

        void bind(Message message) {
            textMessageBody.setText(message.getText());
            // Set profile image if available
        }
    }
}