package com.example.chatapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.Question;
import com.example.hoctap_kiemtra_app.R;

import java.util.List;

public class QManageAdapter extends RecyclerView.Adapter<QManageAdapter.VH> {

    public interface OnActionListener {
        void onEdit(Question q);
        void onDelete(Question q);
    }

    private final List<Question> list;
    private final OnActionListener listener;

    public QManageAdapter(List<Question> list, OnActionListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_manage, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Question q = list.get(pos);

        // ⭐ Nội dung câu hỏi
        h.tvContent.setText(q.getContent());

        // ⭐ Lấy đáp án A B C D từ Map
        if (q.getAnswers() != null) {
            h.tvA.setText("A. " + q.getAnswers().get("A"));
            h.tvB.setText("B. " + q.getAnswers().get("B"));
            h.tvC.setText("C. " + q.getAnswers().get("C"));
            h.tvD.setText("D. " + q.getAnswers().get("D"));
        }

        // ⭐ Hiển thị đáp án đúng
        h.tvCorrect.setText("Đáp án đúng: " + q.getCorrectAnswer());

        h.btnEdit.setOnClickListener(v -> listener.onEdit(q));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(q));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvContent, tvA, tvB, tvC, tvD, tvCorrect;
        Button btnEdit, btnDelete;

        VH(@NonNull View v) {
            super(v);
            tvContent = v.findViewById(R.id.tvQManageContent);
            tvA = v.findViewById(R.id.tvAnsA);
            tvB = v.findViewById(R.id.tvAnsB);
            tvC = v.findViewById(R.id.tvAnsC);
            tvD = v.findViewById(R.id.tvAnsD);
            tvCorrect = v.findViewById(R.id.tvCorrectAns);

            btnEdit = v.findViewById(R.id.btnQManageEdit);
            btnDelete = v.findViewById(R.id.btnQManageDelete);
        }
    }
}
