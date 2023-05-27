package com.gugugu.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.gugugu.myapplication.R;
import com.gugugu.myapplication.adapters.UsersAdapter;
import com.gugugu.myapplication.database.DatabaseHelper;
import com.gugugu.myapplication.databinding.ActivityUsersBinding;
import com.gugugu.myapplication.listeners.UserListener;
import com.gugugu.myapplication.model.User;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends AppCompatActivity implements UserListener {
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUsers();
        setListeners();
    }

    private void setListeners(){
        binding.imageBack.setOnClickListener(v->onBackPressed());
    }
    private void getUsers(){
        loading(true);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        List<User> result = new ArrayList<>();
        List<User> users = databaseHelper.getAll();
        loading(false);
        for(User user: users){
            if(preferenceManager.getString(Constants.KEY_ACCOUNT).equals(user.getAccount())){
                continue;
            }
            result.add(user);
        }
        if(result.size()>0){
            UsersAdapter usersAdapter = new UsersAdapter(result,this);
            binding.usersRecyclerView.setAdapter(usersAdapter);
            binding.usersRecyclerView.setVisibility(View.VISIBLE);
        }else{
            showErrorMessage();
        }

    }
    private void showErrorMessage(){
        binding.textErrorMessage.setText(String.format("%s","没有可选用户"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.progressBar.setVisibility((View.VISIBLE));
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}