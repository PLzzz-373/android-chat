package com.gugugu.myapplication.model;

import java.io.Serializable;

public class User implements Serializable {
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String photo;
    private String name;
    private String account;
    private String password;

    public User(String photo, String name, String account, String password) {
        this.photo = photo;
        this.name = name;
        this.account = account;
        this.password = password;
    }
}
