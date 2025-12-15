package com.example.hoctap_kiemtra_app.utils;

import android.content.Intent;

import com.example.hoctap_kiemtra_app.models.UserModel;

public class AndroidUtil {
    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username",model.getUserName());
        intent.putExtra("userId",model.getUserID());
    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUserName(intent.getStringExtra("username"));
        userModel.setUserID(intent.getStringExtra("userId"));
        return userModel;
    }
}
