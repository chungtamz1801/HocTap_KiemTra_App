package com.example.chatapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Question;
import com.example.chatapp.R;

import java.util.List;

public class QuestionAdapterB extends RecyclerView.Adapter<QuestionAdapterB.VH> {

    public interface OnQuestionActionListener {
        void onEditQuestion(Question q);
        void onDeleteQuestion(Question q);
    }

    private final List<Question> list;
    private final OnQuestionActionListener listener;

    public QuestionAdapterB(List<Question> list, OnQuestionActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_questionb, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Question q = list.get(position);
        holder.tvContent.setText(q.getContent());
        holder.btnEdit.setOnClickListener(v -> listener.onEditQuestion(q));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteQuestion(q));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvContent;
        Button btnEdit, btnDelete;
        VH(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.tvQContent);
            btnEdit = itemView.findViewById(R.id.btnEditQ);
            btnDelete = itemView.findViewById(R.id.btnDeleteQ);
        }
    }


}
