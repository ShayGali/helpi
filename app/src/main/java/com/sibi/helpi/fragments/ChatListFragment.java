package com.sibi.helpi.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ChatListAdapter;
import com.sibi.helpi.models.Chat;
import com.sibi.helpi.viewmodels.ChatViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.List;

public class ChatListFragment extends Fragment {
    private ChatViewModel chatViewModel;
    private RecyclerView recyclerView;
    private ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);
        setupRecyclerView();
        observeViewModel();
    }

    private void initializeViews(View view) {
        recyclerView = view.findViewById(R.id.chat_list_recycler_view);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatListAdapter = new ChatListAdapter(chat -> {
            Bundle bundle = new Bundle();
            bundle.putString("chatId", chat.getChatId());
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_chatListFragment_to_chatMessagesFragment, bundle);
        });
        recyclerView.setAdapter(chatListAdapter);
    }

    private void observeViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        String currentUserId = userViewModel.getCurrentUserId();
        chatViewModel.getChatsList(currentUserId).observe(getViewLifecycleOwner(), this::updateChatList);
    }

    private void updateChatList(List<Chat> chats) {
        if (chats != null) {
            chatListAdapter.setChatList(chats);
        }
    }
}