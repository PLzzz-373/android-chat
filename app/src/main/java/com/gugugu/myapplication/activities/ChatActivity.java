package com.gugugu.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gugugu.myapplication.adapters.ChatAdapter;
import com.gugugu.myapplication.chatgpt.ChatGptResponse;
import com.gugugu.myapplication.chatgpt.Message;
import com.gugugu.myapplication.chatgpt.ProxiedHurlStack;
import com.gugugu.myapplication.database.DatabaseHelper;
import com.gugugu.myapplication.databinding.ActivityChatBinding;
import com.gugugu.myapplication.model.ChatMessage;
import com.gugugu.myapplication.model.User;
import com.gugugu.myapplication.utilities.Config;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

            if(receiverId.equals("chatGpt3.5")){
                getResponse(message);
            }
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
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm:ss a", Locale.getDefault()).format(date);
    }
    private void getResponse(String query) {
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this, new ProxiedHurlStack());
            String URL = "https://api.openai.com/v1/chat/completions";
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "gpt-3.5-turbo");
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("role", "user");
            jsonObject.put("content", query);
            array.put(jsonObject);
            jsonBody.put("messages", array);

            final String requestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.i("VOLLEY", response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("VOLLEY", String.valueOf(error));
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", "Bearer " + Config.API_KEY);
                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    String responseString = new String(response.data, StandardCharsets.UTF_8);
                    // 创建ObjectMapper对象。
                    ObjectMapper mapper = new ObjectMapper();
                    // Json格式字符串转Java对象。
                    try {
                        ChatGptResponse javaEntity = mapper.readValue(responseString, ChatGptResponse.class);
                        String responseMsg = javaEntity.getChoices().get(0).getMessage().getContent();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                responseTV.setText(responseMsg);
                                String timestamp =  getReadableDateTime(new Date());
                                ChatMessage msg = new ChatMessage("chatGpt3.5", preferenceManager.getString(Constants.KEY_ACCOUNT),responseMsg,timestamp);
                                Boolean result = databaseHelper.insertMessage(msg);
                                if(result == true) {
                                    chatMessages.add(msg);
                                    chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                                    binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
                                }

                            }
                        });
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                }
            };

            stringRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 50000;
                }

                @Override
                public void retry(VolleyError error) throws VolleyError {
                    Log.i("VOLLEY", String.valueOf(error));
                }
            });
            requestQueue.add(stringRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}