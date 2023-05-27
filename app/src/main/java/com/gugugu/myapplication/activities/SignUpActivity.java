package com.gugugu.myapplication.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.gugugu.myapplication.R;
import com.gugugu.myapplication.database.DatabaseHelper;
import com.gugugu.myapplication.databinding.ActivitySignInBinding;
import com.gugugu.myapplication.databinding.ActivitySignUpBinding;
import com.gugugu.myapplication.model.User;
import com.gugugu.myapplication.utilities.Constants;
import com.gugugu.myapplication.utilities.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.prefs.PreferenceChangeEvent;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;
    private String encodedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
    }

    private void setListeners(){
        binding.textSignIn.setOnClickListener(v -> onBackPressed());
        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidSignUpDetails()){
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v->{
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    private void showToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
    private void signUp(){
        loading(true);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        String name = binding.inputName.getText().toString();
        String account = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        User user = new User(encodedImage,name,account,password);
        boolean insert = databaseHelper.insert(user);
        loading(false);
        if(insert == true){
            preferenceManager.putBoolean(Constants.KEY_SIGNED_IN, true);
            preferenceManager.putString(Constants.KEY_NAME,name);
            preferenceManager.putString(Constants.KEY_ACCOUNT,account);
            preferenceManager.putString(Constants.KEY_IMAGE, encodedImage);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            showToast("注册失败");
        }
    }
    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            binding.textAddImage.setVisibility(View.GONE);
                            encodedImage = encodedImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private Boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("请上传头像");
            return false;
        }else if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("请输入昵称");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("请输入账号");
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("请输入密码");
            return false;
        }else if (binding.inputConfirmPassword.getText().toString().trim().isEmpty()){
            showToast("请确认密码");
            return false;
        } else if (!binding.inputConfirmPassword.getText().toString().equals(binding.inputPassword.getText().toString())) {
            showToast("两次密码不一致");
            return false;
        }else{
            return true;
        }

    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility((View.VISIBLE));
        }else{
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }
}