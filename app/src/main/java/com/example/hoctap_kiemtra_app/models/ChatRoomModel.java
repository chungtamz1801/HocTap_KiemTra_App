package com.example.hoctap_kiemtra_app.models;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.UUID;

public class ChatRoomModel {
    private String chatRoomID;
    private String chatRoomName;
    private List<String> userIDs;
    private Timestamp lastMessageTimestamp;
    private String lastMessageSenderID;
    private String lastMessage;

    public ChatRoomModel(){

    }

    public ChatRoomModel( String chatRoomName,List<String> userIDs, Timestamp lastMessageTimestamp, String lastMessageSenderID, String lastMessage) {
        this.chatRoomID = UUID.randomUUID().toString().substring(0,8);
        this.chatRoomName = chatRoomName;
        this.userIDs = userIDs;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.lastMessageSenderID = lastMessageSenderID;
        this.lastMessage = lastMessage;
    }
    public String getChatRoomID() {
        return chatRoomID;
    }

    public void setChatRoomID(String chatRoomID) {
        this.chatRoomID = chatRoomID;
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public List<String> getUserIDs() {
        return userIDs;
    }

    public void setUserIDs(List<String> userIDs) {
        this.userIDs = userIDs;
    }

    public Timestamp getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(Timestamp lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public String getLastMessageSenderID() {
        return lastMessageSenderID;
    }

    public void setLastMessageSenderID(String lastMessageSenderID) {
        this.lastMessageSenderID = lastMessageSenderID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
