package com.example.hoctap_kiemtra_app;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class StudentScoreActivity extends AppCompatActivity {

    RecyclerView rv;
    FirebaseFirestore db;
    List<StudentScore> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_score);

        rv = findViewById(R.id.rvScoreList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        String examId = getIntent().getStringExtra("examId");
        Log.d("DEBUG", "examId nhận từ Intent: " + examId);

        if (examId == null) {
            Toast.makeText(this, "Không nhận được examId", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("student_score")
                .whereEqualTo("examId", examId)
                .get()
                .addOnSuccessListener(query -> {
                    Log.d("DEBUG", "Số sinh viên tìm được: " + query.size());
                    list.clear();
                    for (DocumentSnapshot d : query) {
                        StudentScore s = d.toObject(StudentScore.class);
                        list.add(s);
                    }
                    rv.setAdapter(new StudentScoreAdapter(list));
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG", "Lỗi khi lấy dữ liệu: " + e.getMessage());
                    Toast.makeText(this, "Lỗi khi lấy dữ liệu!", Toast.LENGTH_SHORT).show();
                });
    }
}
