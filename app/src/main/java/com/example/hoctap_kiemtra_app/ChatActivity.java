package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap_kiemtra_app.adapters.ChatRecyclerAdapter;
import com.example.hoctap_kiemtra_app.application.MyApplication;
import com.example.hoctap_kiemtra_app.models.ChatMessageModel;

import com.example.hoctap_kiemtra_app.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {
    String roomID;
    String userID;
    String userName;
    //UI
    TextView chatroom_name;
    RecyclerView chat_recycler_view;
    EditText chat_message_input;
    ImageButton message_send_btn;
    ChatRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        MyApplication app = (MyApplication) getApplication();
        userID = app.getUserID();
        userName = app.getUserName();
        getWidget();
        bindDataFromIntent();
        setEvent();
        setupChatRecyclerView();
    }
    private void setupChatRecyclerView()
    {
        ArrayList<ChatMessageModel> messages = new ArrayList<ChatMessageModel>();
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this);
        manager.setReverseLayout(true);
        adapter = new ChatRecyclerAdapter(messages,ChatActivity.this,userID);
        chat_recycler_view.setLayoutManager(manager);
        chat_recycler_view.setAdapter(adapter);

        FirebaseUtil.getMessageCollectionReferrence(roomID)
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.d("Error",error.getMessage());
                            return;
                        }
                        messages.clear();
                        for(QueryDocumentSnapshot doc:value){
                            ChatMessageModel message = doc.toObject(ChatMessageModel.class);
                            if(message!=null) messages.add(message);
                        }
                        adapter.notifyDataSetChanged();

                    }
                });
    }
    private void sendMessage()
    {
        String message = chat_message_input.getText().toString();
        ChatMessageModel messageModel = new ChatMessageModel(message,FirebaseUtil.currentUserID(),userName, Timestamp.now());
        FirebaseUtil.getMessageCollectionReferrence(roomID).document(messageModel.getMessageID()).set(messageModel);
        FirebaseUtil.updateChatRoomModel(roomID,userID,userName+": "+message);
    }
    private void bindDataFromIntent()
    {
        Intent intent = getIntent();
        String roomName = intent.getStringExtra("roomName");
        chatroom_name.setText(roomName);
        roomID = intent.getStringExtra("roomID");
    }
    private void getWidget()
    {
        chatroom_name = findViewById(R.id.chatroom_name_txt);
        chat_recycler_view = findViewById(R.id.chat_recycler_view);
        chat_message_input = findViewById(R.id.chat_message_input);
        message_send_btn = findViewById(R.id.message_send_btn);
    }
    private void setEvent()
    {
        message_send_btn.setOnClickListener(v->sendMessage());
    }
}