package com.example.chatapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoctap_kiemtra_app.R;
import com.example.chatapp.model.Exam;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.VH> {

    public interface OnExamClickListener {
        void onExamClick(Exam exam);
        void onEditExam(Exam exam);
        void onDeleteExam(Exam exam);

    }

    private final List<Exam> list;
    private final OnExamClickListener listener;

    public ExamAdapter(List<Exam> list, OnExamClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Exam e = list.get(position);

        holder.tvName.setText(e.getName());
        holder.tvInfo.setText("Thời gian: " + e.getTime() + " phút | Điểm: " + e.getScore());

        holder.itemView.setOnClickListener(v -> listener.onExamClick(e));
        holder.btnEdit.setOnClickListener(v -> listener.onEditExam(e));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteExam(e));

        // NÚT CHỌN → GIÁO VIÊN CHỌN BÀI KIỂM TRA CHÍNH THỨC
        holder.btnSelect.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Lấy exam hiện tại để kiểm tra trạng thái active
            db.collection("exams").document(e.getId())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (!doc.exists()) return;

                        Boolean active = doc.getBoolean("active");
                        if (active == null) active = false;

                        // Đảo ngược trạng thái
                        boolean newActive = !active;

                        db.collection("exams").document(e.getId())
                                .update("active", newActive)
                                .addOnSuccessListener(aVoid -> {
                                    // Cập nhật SharedPreferences nếu đang active
                                    SharedPreferences prefs = v.getContext().getSharedPreferences("QUIZ_APP", Context.MODE_PRIVATE);
                                    if (newActive) {
                                        prefs.edit().putString("selectedExamId", e.getId()).apply();
                                        Toast.makeText(v.getContext(),
                                                "Đã chọn đề: " + e.getName(),
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        prefs.edit().remove("selectedExamId").apply();
                                        Toast.makeText(v.getContext(),
                                                "Đã hủy chọn đề: " + e.getName(),
                                                Toast.LENGTH_SHORT).show();
                                    }


                                })
                                .addOnFailureListener(ex -> {
                                    Toast.makeText(v.getContext(), "Lỗi: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(ex -> {
                        Toast.makeText(v.getContext(), "Lỗi: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvInfo;
        Button btnEdit, btnDelete, btnSelect;

        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvExamName);
            tvInfo = itemView.findViewById(R.id.tvExamInfo);
            btnEdit = itemView.findViewById(R.id.btnEditExam);
            btnDelete = itemView.findViewById(R.id.btnDeleteExam);
            btnSelect = itemView.findViewById(R.id.btnSelect);
        }
    }
}
