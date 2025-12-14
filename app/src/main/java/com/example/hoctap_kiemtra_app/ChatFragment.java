package com.example.hoctap_kiemtra_app;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hoctap_kiemtra_app.adapters.RecentChatRecyclerAdapter;
import com.example.hoctap_kiemtra_app.application.MyApplication;
import com.example.hoctap_kiemtra_app.models.ChatRoomModel;
import com.example.hoctap_kiemtra_app.utils.FirebaseUtil;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;
    ListenerRegistration registration;

    public ChatFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        if(activity!=null)
        {
            MyApplication app = (MyApplication) activity.getApplication();
            FirebaseUtil.setUserID(app.getUserID());
        }
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);
        setupRecycleView();
        return view;
    }

    @Override
    public void onStop() {
        registration.remove();
        super.onStop();
    }

    void setupRecycleView(){
        List<ChatRoomModel> rooms = new ArrayList<ChatRoomModel>();
        adapter = new RecentChatRecyclerAdapter(rooms,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        registration=FirebaseUtil.getAllChatroomCollectionReference().whereArrayContains("userIDs",FirebaseUtil.currentUserID()).orderBy("lastMessageTimestamp", Query.Direction.DESCENDING).addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Log.d("Error",error.getMessage());
                            return;
                        }
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