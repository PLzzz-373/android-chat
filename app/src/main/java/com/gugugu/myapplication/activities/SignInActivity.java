package com.gugugu.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.gugugu.myapplication.R;
import com.gugugu.myapplication.database.DatabaseHelper;
import com.gugugu.myapplication.databinding.ActivitySignInBinding;
import com.gugugu.myapplication.model.User;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        if(preferenceManager.getBoolean(Constants.KEY_SIGNED_IN)){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();
    }

    private void setListeners(){
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.buttonSignIn.setOnClickListener(v->{
            if(isVailSignInDetails()){
                signIn();
            }
        });
    }
    private  void signIn(){
        loading(true);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String account = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        Boolean result = databaseHelper.checkAccountPassword(account, password);
        if(result == true){
            User one = databaseHelper.getOne(account, password);
            preferenceManager.putBoolean(Constants.KEY_SIGNED_IN,true);
            preferenceManager.putString(Constants.KEY_NAME,one.getName());
            preferenceManager.putString(Constants.KEY_ACCOUNT, account);
            preferenceManager.putString(Constants.KEY_IMAGE,one.getPhoto());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            loading(false);
            showToast("登陆失败，请检查账号密码");
        }
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignIn.setVisibility(View.VISIBLE);
        }
    }
    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private Boolean isVailSignInDetails(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("请输入账号");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("请输入密码");
            return false;
        }else {
            return true;
        }
    }
}