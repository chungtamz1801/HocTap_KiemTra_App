package com.example.hoctap_kiemtra_app.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap_kiemtra_app.ChatActivity;
import com.example.hoctap_kiemtra_app.R;
import com.example.hoctap_kiemtra_app.models.ChatRoomModel;
import com.example.hoctap_kiemtra_app.utils.FirebaseUtil;

import java.util.List;

public class RecentChatRecyclerAdapter extends RecyclerView.Adapter<RecentChatRecyclerAdapter.ChatroomModelViewHolder> {
    Context context;
    List<ChatRoomModel> rooms;

    public RecentChatRecyclerAdapter(List<ChatRoomModel> rooms,Context context) {

        this.context = context;
        this.rooms = rooms;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position) {
        ChatRoomModel room = rooms.get(position);
        boolean lastMessageSentByMe = room.getLastMessageSenderID().equals(FirebaseUtil.currentUserID());
        holder.chatroomNameText.setText(room.getChatRoomName());
        if(lastMessageSentByMe)
            holder.lastMessageText.setText("You: "+ room.getLastMessage());
        else
            holder.lastMessageText.setText(room.getLastMessage());
        holder.lastMessageTime.setText(FirebaseUtil.convertTimestampToString(room.getLastMessageTimestamp()));
        holder.itemView.setOnClickListener(v-> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("roomID", room.getChatRoomID());
            intent.putExtra("roomName", room.getChatRoomName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView chatroomNameText;
        TextView lastMessageText;
        TextView lastMessageTime;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            chatroomNameText = itemView.findViewById(R.id.chatroom_name_txt);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
        }
    }
    @Override
    public int getItemCount(){
        return rooms.size();
    }
}
