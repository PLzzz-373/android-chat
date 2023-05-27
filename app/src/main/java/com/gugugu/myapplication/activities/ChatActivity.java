package com.gugugu.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.gugugu.myapplication.R;
import com.gugugu.myapplication.adapters.ChatAdapter;
import com.gugugu.myapplication.database.DatabaseHelper;
import com.gugugu.myapplication.databinding.ActivityChatBinding;
import com.gugugu.myapplication.model.ChatMessage;
import com.gugugu.myapplication.model.User;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User receiverUser;

    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
        loadReceiverDetails();
        init();
        listenMessage();
    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(
                chatMessages,
                getBitmapFromEncodedString(receiverUser.getPhoto()),
                preferenceManager.getString(Constants.KEY_ACCOUNT)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        databaseHelper = new DatabaseHelper(this);
    }
    private void sendMessage(){
        String senderId =  preferenceManager.getString(Constants.KEY_ACCOUNT);
        String receiverId =  receiverUser.getAccount();
        String message =  binding.inputMessage.getText().toString();
        String timestamp =  getReadableDateTime(new Date());
        ChatMessage msg = new ChatMessage(senderId, receiverId, message, timestamp);
        Boolean result = databaseHelper.insertMessage(msg);
        if(result == true){
            chatMessages.add(msg);
            chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
            binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
        }else{

        }


        binding.inputMessage.setText(null);
    }
    private void listenMessage(){
        eventListener(preferenceManager.getString(Constants.KEY_ACCOUNT), receiverUser.getAccount());
        eventListener(receiverUser.getAccount(), preferenceManager.getString(Constants.KEY_ACCOUNT));

    }
    private void eventListener(String senderId, String receiverId){
        int count = chatMessages.size();
        List<ChatMessage> result = databaseHelper.getAllMsg();
        for(ChatMessage msg : result){
            if(senderId.equals(msg.getSenderId()) && receiverId.equals(msg.getReceiverId())) {
                chatMessages.add(msg);
            }
        }
        Collections.sort(chatMessages, Comparator.comparing(ChatMessage::getDateTime));
        if(count == 0){
            chatAdapter.notifyDataSetChanged();
        }else {
            chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
            binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);

        }
        binding.chatRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private void loadReceiverDetails(){
        receiverUser = (User) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textName.setText(receiverUser.getName());
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v->{
            onBackPressed();
        });
        binding.layoutSend.setOnClickListener(v->{
            sendMessage();
        });
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

}