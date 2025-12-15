package com.example.hoctap_kiemtra_app.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap_kiemtra_app.R;
import com.example.hoctap_kiemtra_app.models.ChatMessageModel;

import java.util.ArrayList;

//Dc su dung truyen du lieu nhan tu Firebase den UI elements
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatModelViewHolder>{
    Context context;
    String userID;
    private ArrayList<ChatMessageModel> messages;
    public ChatRecyclerAdapter(ArrayList<ChatMessageModel> messages, Context context,String userID){
        this.messages = messages;
        this.context = context;
        this.userID = userID;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position) {
        ChatMessageModel message = messages.get(position);
        if(message.getSenderID().equals(userID)){
            holder.leftChatLayout.setVisibility(View.GONE);
            holder.rightChatLayout.setVisibility(View.VISIBLE);
            holder.rightChatTextView.setText(message.getMessage());
        }
        else{
            holder.rightChatLayout.setVisibility(View.GONE);
            holder.leftChatLayout.setVisibility(View.VISIBLE);
            holder.senderUsernameTextView.setText(message.getSenderUsername());
            holder.leftChatTextView.setText(message.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row,parent,false);
        return new ChatModelViewHolder(view);
    }

    public static class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout leftChatLayout,rightChatLayout;
        TextView leftChatTextView,rightChatTextView,senderUsernameTextView;

        public ChatModelViewHolder(@NonNull View itemView){
            super(itemView);

            leftChatLayout = itemView.findViewById(R.id.left_chat_layout);
            rightChatLayout = itemView.findViewById(R.id.right_chat_layout);
            leftChatTextView = itemView.findViewById(R.id.left_chat_textview);
            rightChatTextView = itemView.findViewById(R.id.right_chat_textview);
            senderUsernameTextView = itemView.findViewById(R.id.sender_username_textview);
        }
    }

    @Override
    public int getItemCount(){
        return messages.size();
    }
}
