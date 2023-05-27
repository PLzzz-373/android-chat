package com.gugugu.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Toast;

import com.gugugu.myapplication.R;
import com.gugugu.myapplication.databinding.ActivityMainBinding;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
    }
    private void setListeners(){
        binding.imageSignOut.setOnClickListener(v->{
            signOut();
        });
        binding.fabNewChat.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), UsersActivity.class));
        });
    }
    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }
    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void signOut(){
        showToast("退出中...");
        preferenceManager.clear();
        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }
}