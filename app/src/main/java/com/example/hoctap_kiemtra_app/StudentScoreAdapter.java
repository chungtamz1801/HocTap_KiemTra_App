package com.example.hoctap_kiemtra_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StudentScoreAdapter extends RecyclerView.Adapter<StudentScoreAdapter.ViewHolder> {

    List<StudentScore> list;

    public StudentScoreAdapter(List<StudentScore> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student_score, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int i) {
        StudentScore s = list.get(i);

        h.tvStudentId.setText("Mã Sinh Viên: "+s.id);
        h.tvStudentName.setText(s.name);
        h.tvScore.setText("Điểm: " + s.score);

        // Thời gian làm bài
        if (s.timeTaken != null && !s.timeTaken.isEmpty()) {
            h.tvTime.setText("Thời gian làm: " + s.timeTaken);
        } else {
            h.tvTime.setText("Thời gian làm: --");
        }

        // Thời điểm nộp bài (timestamp)
        if (s.timestamp != null) {
            Date date = s.timestamp.toDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            h.tvSubmitTime.setText("Nộp lúc: " + sdf.format(date));
        } else {
            h.tvSubmitTime.setText("Nộp lúc: --");
        }
    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName, tvScore, tvTime, tvSubmitTime, tvStudentId;

        ViewHolder(View v) {
            super(v);
            tvStudentId = v.findViewById(R.id.tvStudentId);
            tvStudentName = v.findViewById(R.id.tvStudentName);
            tvScore = v.findViewById(R.id.tvScore);
            tvTime = v.findViewById(R.id.tvTime);
            tvSubmitTime = v.findViewById(R.id.tvSubmitTime);
        }
    }
}
