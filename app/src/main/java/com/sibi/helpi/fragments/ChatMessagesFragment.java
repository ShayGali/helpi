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
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ChatMessagesAdapter;
import com.sibi.helpi.viewmodels.ChatViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.ArrayList;

public class ChatMessagesFragment extends Fragment {
    private ChatViewModel chatViewModel;
    private RecyclerView recyclerView;
    private ChatMessagesAdapter chatMessagesAdapter;
    private EditText messageInput;
    private ImageButton sendButton;
    private String chatId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_messages, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatId = getArguments() != null ? getArguments().getString("chatId") : null;
        if (chatId == null) {
            requireActivity().onBackPressed();
            return;
        }

        initializeViews(view);
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.messages_recycler_view);
        messageInput = view.findViewById(R.id.message_input);
        sendButton = view.findViewById(R.id.send_button);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatMessagesAdapter = new ChatMessagesAdapter(new ArrayList<>());
        recyclerView.setAdapter(chatMessagesAdapter);
    }

    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                UserViewModel userViewModel = UserViewModel.getInstance();
                String currentUserId = userViewModel.getUserId();
                chatViewModel.sendMessage(currentUserId, chatId, messageText);
                messageInput.setText("");
            }
        });
    }

    private void observeViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        chatViewModel.getChatMessages(chatId).observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                chatMessagesAdapter.updateMessages(messages);
                recyclerView.scrollToPosition(messages.size() - 1);
            }
        });
    }
}