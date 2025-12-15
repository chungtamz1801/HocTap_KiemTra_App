package com.example.hoctap_kiemtra_app.application;

import android.app.Application;

public class MyApplication extends Application {
    private String userID = "";
    private String userName = "";

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
