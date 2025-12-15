package com.example.hoctap_kiemtra_app.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.UUID;

public class UserModel{
    private String userID;
    private String userName;
    private String password;
    private Timestamp createdTimestamp;

    public UserModel(){

    }

    public UserModel(String userName, String password, Timestamp createdTimestamp) {
        this.userID = UUID.randomUUID().toString().substring(0,8);
        this.userName = userName;
        this.password = password;
        this.createdTimestamp = createdTimestamp;
    }
    public String getUserID(){
        return userID;
    }
    public void setUserID(String userID){
        this.userID = userID;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }
}
