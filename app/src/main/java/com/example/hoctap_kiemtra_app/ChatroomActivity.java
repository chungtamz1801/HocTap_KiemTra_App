package com.example.hoctap_kiemtra_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap_kiemtra_app.adapters.RecentChatRecyclerAdapter;
import com.example.hoctap_kiemtra_app.models.ChatRoomModel;
import com.example.hoctap_kiemtra_app.utils.FirebaseUtil;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatroomActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    ListenerRegistration registration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chatroom);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getDataFromSharedPreferences();
        recyclerView = findViewById(R.id.chatroom_lst);
        setupRecycleView();
    }
    void getDataFromSharedPreferences(){
        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
        String userID = prefs.getString("LECTURER_ID","");
        FirebaseUtil.setUserID(userID);
    }
    void setupRecycleView(){
        List<ChatRoomModel> rooms = new ArrayList<ChatRoomModel>();
        adapter = new RecentChatRecyclerAdapter(rooms,ChatroomActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatroomActivity.this));
        recyclerView.setAdapter(adapter);
        //Sử dụng whereArrayContains() để kiểm tra ID của người dùng đăng nhập có trong danh sách ID của chatroom không
        //Sử dụng orderBy() để sắp xếp các chatroom nhằm hiển thị chatroom mới cập nhật
        //Sử dụng addSnapshotListener() để bắt sự kiện khi có dữ liệu cập nhật
        registration= FirebaseUtil.getAllChatroomCollectionReference().whereArrayContains("userIDs",FirebaseUtil.currentUserID()).orderBy("lastMessageTimestamp", Query.Direction.DESCENDING).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.d("Error",error.getMessage());
                            return;
                        }
                        // Clear data để cập nhật lại từ đầu
                        rooms.clear();

                        for(QueryDocumentSnapshot doc:value){
                            ChatRoomModel room = doc.toObject(ChatRoomModel.class);
                            if(room!=null) rooms.add(room);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
        );

    }
}