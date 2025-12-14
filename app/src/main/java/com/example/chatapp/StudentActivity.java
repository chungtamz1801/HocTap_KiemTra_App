package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.hoctap_kiemtra_app.R;
public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        Button btnStart = findViewById(R.id.btnStartQuiz);

        Button btnReview = findViewById(R.id.btnReviewResult);

        // 1. NÚT START: Gọi MainActivity (Logic cũ: Tự lấy đề đầu tiên để thi)
        btnStart.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Lấy exam nào đang active
            db.collection("exams")
                    .whereEqualTo("active", true)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (querySnapshot.isEmpty()) {
                            // Không có exam active
                            Toast.makeText(this, "Chưa có đề thi đang mở hoặc đã hết thời gian làm bài!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Lấy exam đầu tiên đang active
                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                        String examId = doc.getId();

                        // Mở activity làm bài
                        Intent intent = new Intent(StudentActivity.this, DoQuizActivity.class);
                        intent.putExtra("examId", examId);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });




        // 3. NÚT REVIEW: Xem lại kết quả vừa thi
        btnReview.setOnClickListener(v -> {
            if (QuestionDataHolder.getInstance().getListQuestions() == null) {
                Toast.makeText(this, "Bạn chưa làm bài nào!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(StudentActivity.this, ReviewActivity.class);
                startActivity(intent);
            }
        });
    }
}