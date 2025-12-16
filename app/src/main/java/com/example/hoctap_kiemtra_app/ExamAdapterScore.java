package com.example.hoctap_kiemtra_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.model.Exam;

import java.util.List;

public class ExamAdapterScore
        extends RecyclerView.Adapter<ExamAdapterScore.ViewHolder> {

    List<Exam> list;
    OnExamClickListener listener;

    public interface OnExamClickListener {
        void onClick(Exam exam);
    }

    public ExamAdapterScore(List<Exam> list, OnExamClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam_score, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder h, int position) {

        Exam e = list.get(position);

        // 🔥 GÁN Ở ĐÂY
        h.tvExamName.setText(e.getName());
        h.tvExamTime.setText("⏱ " + e.getTime() + " phút");
        h.tvExamScore.setText("🎯 " + e.getScore() + " điểm");

        h.itemView.setOnClickListener(v -> listener.onClick(e));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // ================= VIEW HOLDER =================
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvExamName, tvExamTime, tvExamScore;

        ViewHolder(@NonNull View v) {
            super(v);

            // 🔥 KHAI BÁO Ở ĐÂY
            tvExamName  = v.findViewById(R.id.tvExamName);
            tvExamTime  = v.findViewById(R.id.tvExamTime);
            tvExamScore = v.findViewById(R.id.tvExamScore);
        }
    }
}
