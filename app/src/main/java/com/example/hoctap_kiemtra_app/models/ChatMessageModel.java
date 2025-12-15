package com.example.hoctap_kiemtra_app.models;

import java.util.UUID;
import com.google.firebase.Timestamp;


public class ChatMessageModel {
    private String messageID;
    private String message;
    private String senderID;
    private String senderUsername;
    private Timestamp timestamp;

    public ChatMessageModel() {
    }

    public ChatMessageModel(String message, String senderID, String senderUsername,Timestamp timestamp) {
        this.messageID = UUID.randomUUID().toString().substring(0,8);
        this.message = message;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.senderUsername = senderUsername;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
