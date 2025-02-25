package com.sibi.helpi.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ChatMessagesAdapter;
import com.sibi.helpi.viewmodels.ChatViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatMessagesFragment extends Fragment {
    private UserViewModel userViewModel;
    private ChatViewModel chatViewModel;
    private RecyclerView recyclerView;
    private ChatMessagesAdapter chatMessagesAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private String chatId;
    ImageButton backButton;
    TextView partnerNameText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        chatId = getArguments() != null ? getArguments().getString("chatId") : null;
        if (chatId == null) {
            requireActivity().onBackPressed();
            return;
        }

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();

        String currentUserId = userViewModel.getCurrentUserId();
        chatViewModel.markMessagesAsRead(chatId, currentUserId);
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.messages_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
        backButton = view.findViewById(R.id.back_button);
        partnerNameText = view.findViewById(R.id.chat_partner_name);

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        // Get partner name from chat
        chatViewModel.getChatById(chatId).observe(getViewLifecycleOwner(), chat -> {
            if (chat != null) {
                // First try to use chatPartnerName
                String partnerName = chat.getChatPartnerName(userViewModel.getCurrentUserId());
                if (partnerName != null && !partnerName.isEmpty()) {
                    partnerNameText.setText(partnerName);
                    return;
                }

                // If no chatPartnerName, try to get it from participants
                List<String> participants = chat.getParticipants();
                if (participants != null && !participants.isEmpty()) {
                    try {
                        String currentUserId = userViewModel.getCurrentUserId();
                        String partnerId = participants.stream()
                                .filter(id -> id != null && !id.equals(currentUserId))
                                .findFirst()
                                .orElse(null);

                        if (partnerId != null) {
                            userViewModel.getUserByIdLiveData(partnerId)
                                    .observe(getViewLifecycleOwner(), user -> {
                                        if (user != null) {
                                            String name = user.getFullName();
                                            partnerNameText.setText(name != null ? name : "Chat Partner");
                                        }
                                    });
                        } else {
                            partnerNameText.setText("Chat Partner");
                        }
                    } catch (Exception e) {
                        partnerNameText.setText("Chat Partner");
                    }
                } else {
                    partnerNameText.setText("Chat Partner");
                }
            } else {
                partnerNameText.setText("Chat Partner");
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMessagesAdapter = new ChatMessagesAdapter(new ArrayList<>(0), userViewModel.getCurrentUserId());
        recyclerView.setAdapter(chatMessagesAdapter);
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                String currentUserId = userViewModel.getCurrentUserId();
                chatViewModel.sendMessage(currentUserId, chatId, messageText);
                messageInput.setText("");
            }
        });
    }

    private void observeViewModel() {
        chatViewModel.getChatMessages(chatId).observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                chatMessagesAdapter.updateMessages(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }
}