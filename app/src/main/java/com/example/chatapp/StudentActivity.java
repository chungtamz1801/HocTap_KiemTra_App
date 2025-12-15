package com.example.chatapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.example.hoctap_kiemtra_app.R;

public class StudentActivity extends AppCompatActivity {

    private LinearLayout layoutStats;
    private TextView tvTotalExams, tvCompleted;
    private Button btnStart, btnReview;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        db = FirebaseFirestore.getInstance();

        btnStart = findViewById(R.id.btnStartQuiz);
        btnReview = findViewById(R.id.btnReviewResult);
        layoutStats = findViewById(R.id.layoutStats);
        tvTotalExams = findViewById(R.id.tvTotalExams);
        tvCompleted = findViewById(R.id.tvCompleted);

        // Load thống kê
        loadStudentStatistics();

        // Nút vào thi
        btnStart.setOnClickListener(v -> {
            db.collection("exams")
                    .whereEqualTo("active", true)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            Toast.makeText(this, "Chưa có đề thi đang mở!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String examId = doc.getId();

                        Intent intent = new Intent(StudentActivity.this, DoQuizActivity.class);
                        intent.putExtra("examId", examId);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // Nút xem lại
        btnReview.setOnClickListener(v -> {
            if (QuestionDataHolder.getInstance().getListQuestions() == null) {
                Toast.makeText(this, "Bạn chưa làm bài nào!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(StudentActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật thống kê mỗi khi quay lại màn hình
        loadStudentStatistics();
    }

    private void loadStudentStatistics() {
        SharedPreferences prefs = getSharedPreferences("QUIZ_APP", MODE_PRIVATE);
        String studentId = prefs.getString("STUDENT_ID", null);

        if (studentId == null) {
            layoutStats.setVisibility(View.GONE);
            return;
        }

        // Đếm tổng số bài thi
        db.collection("exams")
                .get()
                .addOnSuccessListener(examsSnapshot -> {
                    int totalExams = examsSnapshot.size();
                    tvTotalExams.setText(String.valueOf(totalExams));

                    // Đếm số bài đã hoàn thành
                    db.collection("student_score")
                            .whereEqualTo("id", studentId)
                            .get()
                            .addOnSuccessListener(scoresSnapshot -> {
                                int completedExams = scoresSnapshot.size();
                                tvCompleted.setText(String.valueOf(completedExams));

                                // Hiển thị thống kê
                                layoutStats.setVisibility(View.VISIBLE);
                            })
                            .addOnFailureListener(e -> {
                                layoutStats.setVisibility(View.GONE);
                            });
                })
                .addOnFailureListener(e -> {
                    layoutStats.setVisibility(View.GONE);
                });
    }
}