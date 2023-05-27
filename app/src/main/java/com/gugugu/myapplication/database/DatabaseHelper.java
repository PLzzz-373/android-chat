package com.gugugu.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.gugugu.myapplication.model.ChatMessage;
import com.gugugu.myapplication.model.User;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String databaseName = "chat.db";
    public DatabaseHelper(@Nullable Context context) {
        super(context, databaseName, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table user(name varchar(32), account varchar(32) not null primary key, password varchar(32), photo varchar(500))");
        sqLiteDatabase.execSQL("create table chat(sender_id varchar(50), receiver_id varchar(50), message varchar(500), date_time varchar(50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table if exists user");
        sqLiteDatabase.execSQL("drop table if exists chat");
    }

    public Boolean insert(User user){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("photo", user.getPhoto());
        contentValues.put("name", user.getName());
        contentValues.put("account",user.getAccount());
        contentValues.put("password", user.getPassword());
        long result = sqLiteDatabase.insert("user",null,contentValues);
        sqLiteDatabase.close();
        if(result == -1){
            return false;
        }
        return true;
    }
    public Boolean checkAccount(String account){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select account from user where account = ?", new String[]{account});
        if(cursor.getCount() > 0){
            sqLiteDatabase.close();
            return true;
        }else {
            sqLiteDatabase.close();
            return false;
        }
    }

    public Boolean checkAccountPassword(String account, String password){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select account from user where account = ? and password = ?", new String[]{account, password});
        if(cursor.getCount()>0){
            sqLiteDatabase.close();
            return true;
        }else {
            sqLiteDatabase.close();
            return false;
        }

    }

    public User getOne(String account, String password){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user where account = ? and password = ?",new String[]{account, password});
        cursor.moveToFirst();
        int nameIndex = cursor.getColumnIndex("name");
        int accountIndex = cursor.getColumnIndex("account");
        int passwordIndex = cursor.getColumnIndex("password");
        int photoIndex = cursor.getColumnIndex("photo");

        User user = new User(cursor.getString(photoIndex),cursor.getString(nameIndex), cursor.getString(accountIndex),cursor.getString(passwordIndex));
        cursor.close();
        sqLiteDatabase.close();
        return user;
    }

    public List<User> getAll(){
        List<User> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from user",null);
        int nameIndex = cursor.getColumnIndex("name");
        int accountIndex = cursor.getColumnIndex("account");
        int passwordIndex = cursor.getColumnIndex("password");
        int photoIndex = cursor.getColumnIndex("photo");

        while(cursor.moveToNext()){
            User user = new User(cursor.getString(photoIndex),cursor.getString(nameIndex), cursor.getString(accountIndex),cursor.getString(passwordIndex));
            list.add(user);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

    public Boolean insertMessage(ChatMessage msg){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues =  new ContentValues();
        contentValues.put("sender_id", msg.getSenderId());
        contentValues.put("receiver_id", msg.getReceiverId());
        contentValues.put("message",msg.getMessage());
        contentValues.put("date_time", msg.getDateTime());
        long result = sqLiteDatabase.insert("chat",null,contentValues);
        sqLiteDatabase.close();
        if(result == -1){
            return false;
        }
        return true;
    }

    public List<ChatMessage> getAllMsg(){
        List<ChatMessage> list =  new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from chat",null);
        int senderIdIndex = cursor.getColumnIndex("sender_id");
        int receiverIdIndex = cursor.getColumnIndex("receiver_id");
        int messageIndex = cursor.getColumnIndex("message");
        int datetimeIndex = cursor.getColumnIndex("date_time");

        while(cursor.moveToNext()){
            ChatMessage msg = new ChatMessage(cursor.getString(senderIdIndex),cursor.getString(receiverIdIndex), cursor.getString(messageIndex),cursor.getString(datetimeIndex));
            list.add(msg);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }
}
