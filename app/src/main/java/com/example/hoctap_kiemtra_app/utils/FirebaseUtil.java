package com.example.hoctap_kiemtra_app.utils;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseUtil {
    private static String userID = "";
    public static String currentUserID(){
        return userID;
    }

    public static void setUserID(String id){
        userID = id;
    }
    public static CollectionReference getAllUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }
    public static CollectionReference getAllChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static Query getUserOnLogin(String userName, String password){
        return FirebaseFirestore.getInstance().collection("users").whereEqualTo("userName",userName).whereEqualTo("password",password);
    }
    public static DocumentReference getOtherUserFromChatroom(List<String> userIDs){
        if(userIDs.get(0).equals(FirebaseUtil.currentUserID())){
            return getAllUserCollectionReference().document(userIDs.get(1));
        }
        else{
            return getAllUserCollectionReference().document(userIDs.get(0));
        }
    }
    public static CollectionReference getMessageCollectionReferrence(String chatRoomID)
    {
        return getAllChatroomCollectionReference().document(chatRoomID).collection("chats");
    }
    public static String convertTimestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static Boolean updateChatRoomModel(String chatRoomID,String userID,String message)
    {
        Map<String,Object> chatRoomUpdates = new HashMap<>();
        chatRoomUpdates.put("lastMessage",message);
        chatRoomUpdates.put("lastMessageSenderID",userID);
        chatRoomUpdates.put("lastMessageTimestamp",Timestamp.now());
        return getAllChatroomCollectionReference().document(chatRoomID).update(chatRoomUpdates).isSuccessful();
    }
}
