package com.example.hoctap_kiemtra_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExamListActivity extends AppCompatActivity {

    RecyclerView rv;
    FirebaseFirestore db;
    List<Exam> examList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list_score);

        rv = findViewById(R.id.rvExamList);
        rv.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        db.collection("exams")
                .get()
                .addOnSuccessListener(query -> {
                    examList.clear();
                    for (DocumentSnapshot d : query) {
                        Exam e = d.toObject(Exam.class);
                        e.examId = d.getId(); // Lấy document ID làm examId
                        examList.add(e);
                    }
                    rv.setAdapter(new ExamAdapterScore(examList, exam -> {
                        Log.d("DEBUG", "Click examId: " + exam.examId);
                        Intent i = new Intent(ExamListActivity.this, StudentScoreActivity.class);
                        i.putExtra("examId", exam.examId); // Truyền đúng examId
                        startActivity(i);
                    }));
                })
                .addOnFailureListener(e -> {
                    Log.e("DEBUG", "Lỗi khi load exams: " + e.getMessage());
                });
    }
}
